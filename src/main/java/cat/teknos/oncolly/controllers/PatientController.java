package cat.teknos.oncolly.controllers;

import cat.teknos.oncolly.dtos.user.UpdateProfileRequest;
import cat.teknos.oncolly.services.UserService;
import cat.teknos.oncolly.services.PatientService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired private PatientRepository patientRepo;
    @Autowired private ActivityRepository activityRepo;
    @Autowired private DoctorPatientRepository doctorPatientRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EntityMapper mapper;
    @Autowired private PatientService patientService;
    @Autowired private UserService userService;

    // GET ALL PATIENTS (For the logged-in Doctor)
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getMyPatients(Authentication auth) {
        String email = auth.getName();
        logger.info("Fetching patients for doctor: {}", email);
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
        logger.info("Doctor {} creating new patient: {}", auth.getName(), request.email());
        try {
            patientService.createPatient(request, auth.getName());
            return ResponseEntity.ok("Patient created and assigned to you.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Error creating patient: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{patientId}/activities")
    public ResponseEntity<?> getPatientActivities(@PathVariable UUID patientId, Authentication auth) {
        logger.info("Doctor {} requesting activities for patient {}", auth.getName(), patientId);

        // Identify the Doctor
        AppUser loggedInUser = userRepo.findByEmail(auth.getName()).orElseThrow();

        // We prevent Doctor A from spying on Doctor B's patients.
        boolean isLinked = doctorPatientRepo.existsByDoctorIdAndPatientIdAndIsDeletedFalse(loggedInUser.getId(), patientId);

        if (!isLinked) {
            logger.warn("Access denied: Doctor {} is not assigned to patient {}", auth.getName(), patientId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not assigned to this patient.");
        }

        // Fetch Data
        List<ActivityResponse> response = activityRepo.findByPatientIdAndIsDeletedFalse(patientId).stream()
                .map(mapper::toActivityResponse)
                .collect(Collectors.toList());


        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getPatientProfile(Authentication auth) {
        logger.info("User {} requesting profile", auth.getName());
        AppUser user = userRepo.findByEmail(auth.getName()).orElseThrow();

        if (user instanceof Patient patient) {
            return ResponseEntity.ok(mapper.toPatientResponse(patient));
        } else if (user instanceof Doctor doctor) {
            return ResponseEntity.ok(mapper.toPatientResponse(doctor));
        } else {
            logger.error("Unknown user type for email: {}", auth.getName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown user type");
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request, Authentication auth) {
        logger.info("User {} updating profile", auth.getName());
        try {
            AppUser updatedUser = userService.updateProfile(auth.getName(), request);
            
            if (updatedUser instanceof Patient patient) {
                return ResponseEntity.ok(mapper.toPatientResponse(patient));
            } else if (updatedUser instanceof Doctor doctor) {
                return ResponseEntity.ok(mapper.toPatientResponse(doctor));
            } else {
                return ResponseEntity.ok("Profile updated");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Error updating profile: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID patientId, Authentication auth) {
        logger.info("Doctor {} deleting patient {}", auth.getName(), patientId);
        try {
            patientService.removePatientFromDoctor(patientId, auth.getName());
            return ResponseEntity.ok("Patient removed from your list.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Error deleting patient: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}