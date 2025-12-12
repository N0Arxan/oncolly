package cat.teknos.oncolly.controllers;

import cat.teknos.oncolly.dtos.activity.ActivityResponse;
import cat.teknos.oncolly.dtos.activity.CreateActivityRequest;
import cat.teknos.oncolly.models.Activity;
import cat.teknos.oncolly.models.Patient;
import cat.teknos.oncolly.models.AppUser;
import cat.teknos.oncolly.repositories.ActivityRepository;
import cat.teknos.oncolly.repositories.DoctorPatientRepository;
import cat.teknos.oncolly.repositories.UserRepository;
import cat.teknos.oncolly.utils.EntityMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ActivityController {

    @Autowired private ActivityRepository activityRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private DoctorPatientRepository doctorPatientRepo;
    @Autowired private EntityMapper mapper;

    // ==========================================
    // PATIENT: Get Activities
    // ==========================================
    @GetMapping("/activities")
    public ResponseEntity<List<ActivityResponse>> getActivities(Authentication auth) {
        // Identify the Patient
        Patient patient = (Patient) userRepo.findByEmail(auth.getName()).orElseThrow();
        List<Activity> activities = activityRepo.findByPatientIdAndIsDeletedFalse(patient.getId());
        List<ActivityResponse> responseList = activities.stream()
                .map(mapper::toActivityResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    // ==========================================
    // PATIENT: Create Activity
    // ==========================================
    @PostMapping("/activities")
    public ResponseEntity<?> createActivity(@Valid @RequestBody CreateActivityRequest request, Authentication auth) {

        // Identify the Patient
        Patient patient = (Patient) userRepo.findByEmail(auth.getName()).orElseThrow();

        // Idempotency Check
        if (activityRepo.existsById(request.id())) {
            return ResponseEntity.ok("Activity already synced.");
        }

        // Create Entity
        Activity activity = new Activity();
        activity.setId(request.id());
        activity.setPatient(patient);
        activity.setActivityType(request.activityType());
        activity.setValue(request.value());
        activity.setOccurredAt(request.occurredAt());

        activityRepo.save(activity);

        return ResponseEntity.ok("Activity saved.");
    }

    // ==========================================
    // 3. PATIENT: Delete Activity (Soft Delete)
    // ==========================================
    @DeleteMapping("/activities/{activityId}")
    public ResponseEntity<?> deleteActivity(@PathVariable UUID activityId, Authentication auth) {

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // Ensure the patient owns this activity
        String currentUserEmail = auth.getName();
        if (!activity.getPatient().getEmail().equals(currentUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own activities.");
        }

        // Soft Delete
        activity.setDeleted(true);
        activityRepo.save(activity);

        return ResponseEntity.ok("Activity deleted.");
    }
}