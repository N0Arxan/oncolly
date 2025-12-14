package cat.teknos.oncolly.controllers;

import cat.teknos.oncolly.dtos.activity.ActivityResponse;
import cat.teknos.oncolly.dtos.patient.CreatePatientRequest;
import cat.teknos.oncolly.dtos.patient.PatientResponse;
import cat.teknos.oncolly.models.AppUser;
import cat.teknos.oncolly.models.Doctor;
import cat.teknos.oncolly.models.DoctorPatient;
import cat.teknos.oncolly.models.keys.DoctorPatientKey;
import cat.teknos.oncolly.models.Patient;
import cat.teknos.oncolly.models.enums.Role;
import cat.teknos.oncolly.repositories.*;
import cat.teknos.oncolly.utils.EntityMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired private PatientRepository patientRepo;
    @Autowired private ActivityRepository activityRepo;
    @Autowired private DoctorPatientRepository doctorPatientRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EntityMapper mapper;

    // GET ALL PATIENTS (For the logged-in Doctor)
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getMyPatients(Authentication auth) {
        String email = auth.getName();
        Doctor doctor = (Doctor) userRepo.findByEmail(email).orElseThrow();

        List<DoctorPatient> relations = doctorPatientRepo.findByDoctorIdAndIsDeletedFalse(doctor.getId());

        List<PatientResponse> responseList = relations.stream()
                .map(DoctorPatient::getPatient)
                .map(mapper::toPatientResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PostMapping
    public ResponseEntity<?> createPatient(@Valid @RequestBody CreatePatientRequest request, Authentication auth) {
        if (userRepo.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already taken");
        }

        Patient patient = new Patient();
        patient.setEmail(request.email());
        patient.setPasswordHash(passwordEncoder.encode(request.password()));
        patient.setPhoneNumber(request.phoneNumber());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setRole(Role.PATIENT);

        // Save Patient
        Patient savedPatient = patientRepo.save(patient);

        // Link Patient to Current Doctor
        String doctorEmail = auth.getName();
        Doctor doctor = (Doctor) userRepo.findByEmail(doctorEmail).orElseThrow();

        DoctorPatient link = new DoctorPatient();
        link.setId(new DoctorPatientKey(doctor.getId(), savedPatient.getId()));
        link.setDoctor(doctor);
        link.setPatient(savedPatient);

        doctorPatientRepo.save(link);

        return ResponseEntity.ok("Patient created and assigned to you.");
    }

    @GetMapping("/{patientId}/activities")
    public ResponseEntity<?> getPatientActivities(@PathVariable UUID patientId, Authentication auth) {

        // Identify the Doctor
        AppUser loggedInUser = userRepo.findByEmail(auth.getName()).orElseThrow();

        // We prevent Doctor A from spying on Doctor B's patients.
        boolean isLinked = doctorPatientRepo.existsByDoctorIdAndPatientIdAndIsDeletedFalse(loggedInUser.getId(), patientId);

        if (!isLinked) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not assigned to this patient.");
        }

        // Fetch Data
        List<ActivityResponse> response = activityRepo.findByPatientId(patientId).stream()
                .map(mapper::toActivityResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getPatientProfile(Authentication auth) {
        Patient patient = (Patient) userRepo.findByEmail(auth.getName()).orElseThrow();
        PatientResponse response = mapper.toPatientResponse(patient);
        return ResponseEntity.ok(response);
    }
}