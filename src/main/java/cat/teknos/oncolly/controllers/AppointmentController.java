package cat.teknos.oncolly.controllers;

import cat.teknos.oncolly.dtos.appointment.AppointmentResponse;
import cat.teknos.oncolly.dtos.appointment.CreateAppointmentRequest;
import cat.teknos.oncolly.models.Appointment;
import cat.teknos.oncolly.models.Doctor;
import cat.teknos.oncolly.models.Patient;
import cat.teknos.oncolly.repositories.AppointmentRepository;
import cat.teknos.oncolly.repositories.PatientRepository;
import cat.teknos.oncolly.repositories.UserRepository;
import cat.teknos.oncolly.utils.EntityMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired private AppointmentRepository appointmentRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PatientRepository patientRepo;
    @Autowired private EntityMapper mapper;

    // ==========================================
    // DOCTOR: Get My Appointments
    // ==========================================
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Authentication auth) {
        Doctor doctor = (Doctor) userRepo.findByEmail(auth.getName()).orElseThrow();

        // Fetch active appointments
        // Ideally, create a method 'findByDoctorIdAndIsDeletedFalse' in Repository
        List<AppointmentResponse> response = appointmentRepo.findByDoctorId(doctor.getId()).stream()
                .filter(a -> !a.isDeleted())
                .map(mapper::toAppointmentResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // 2. DOCTOR: Create Appointment (With Conflict Check)
    // ==========================================
    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentRequest request, Authentication auth) {

        Doctor doctor = (Doctor) userRepo.findByEmail(auth.getName()).orElseThrow();
        Patient patient = patientRepo.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // CONFLICT CHECK
        boolean hasConflict = appointmentRepo.existsByDoctorIdAndTimeOverlap(
                doctor.getId(),
                request.startTime(),
                request.endTime()
        );

        if (hasConflict) {
            return ResponseEntity.badRequest().body("Time slot is already booked!");
        }

        // Save Appointment
        Appointment appt = new Appointment();
        appt.setId(request.id()); // Use Android UUID
        appt.setDoctor(doctor);
        appt.setPatient(patient);
        appt.setStartTime(request.startTime());
        appt.setEndTime(request.endTime());
        appt.setTitle(request.title());
        appt.setNotes(request.doctorNotes());

        // Status defaults to CONFIRMED in Entity, or set it here explicitly

        appointmentRepo.save(appt);

        return ResponseEntity.ok("Appointment scheduled.");
    }

    // ==========================================
    // 3. DOCTOR: Delete Appointment
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id, Authentication auth) {

        Appointment appt = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Check ownership
        Doctor doctor = (Doctor) userRepo.findByEmail(auth.getName()).orElseThrow();
        if (!appt.getDoctor().getId().equals(doctor.getId())) {
            return ResponseEntity.status(403).body("You cannot delete appointments that aren't yours.");
        }

        // Soft Delete
        appt.setDeleted(true);
        appointmentRepo.save(appt);

        return ResponseEntity.ok("Appointment cancelled.");
    }
}