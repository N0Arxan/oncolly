package cat.teknos.oncolly.services;

import cat.teknos.oncolly.dtos.patient.CreatePatientRequest;
import cat.teknos.oncolly.models.AppUser;
import cat.teknos.oncolly.models.Doctor;
import cat.teknos.oncolly.models.DoctorPatient;
import cat.teknos.oncolly.models.Patient;
import cat.teknos.oncolly.models.enums.Role;
import cat.teknos.oncolly.models.keys.DoctorPatientKey;
import cat.teknos.oncolly.repositories.DoctorPatientRepository;
import cat.teknos.oncolly.repositories.PatientRepository;
import cat.teknos.oncolly.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorPatientRepository doctorPatientRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createPatient(CreatePatientRequest request, String doctorEmail) {
        logger.info("Attempting to create patient with email: {}", request.email());

        // 1. Validate if email exists
        if (userRepo.findByEmail(request.email()).isPresent()) {
            logger.warn("Creation failed: Email {} already exists", request.email());
            throw new IllegalArgumentException("Email already taken");
        }

        // 2. Fetch the current Doctor
        AppUser user = userRepo.findByEmail(doctorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        if (!(user instanceof Doctor)) {
            logger.error("Security/Logic Error: User {} attempted to create patient but is not a Doctor.", doctorEmail);
            throw new IllegalStateException("Current user is not a doctor.");
        }
        Doctor doctor = (Doctor) user;

        // 3. Create and Save Patient
        Patient patient = new Patient();
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setEmail(request.email());
        patient.setPasswordHash(passwordEncoder.encode(request.password()));
        patient.setPhoneNumber(request.phoneNumber());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setRole(Role.PATIENT);

        Patient savedPatient = patientRepo.save(patient);
        logger.info("Patient saved with ID: {}", savedPatient.getId());

        // 4. Link Patient to Doctor
        DoctorPatient link = new DoctorPatient();
        link.setId(new DoctorPatientKey(doctor.getId(), savedPatient.getId()));
        link.setDoctor(doctor);
        link.setPatient(savedPatient);

        doctorPatientRepo.save(link);
        logger.info("Patient {} successfully linked to Doctor {}", savedPatient.getId(), doctor.getId());
    }

    @Transactional
    public void removePatientFromDoctor(UUID patientId, String doctorEmail) {
        // 1. Fetch Doctor
        AppUser user = userRepo.findByEmail(doctorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        if (!(user instanceof Doctor)) {
            throw new IllegalStateException("Current user is not a doctor.");
        }
        Doctor doctor = (Doctor) user;

        // 2. Find Relationship
        DoctorPatientKey key = new DoctorPatientKey(doctor.getId(), patientId);
        DoctorPatient relation = doctorPatientRepo.findById(key)
                .orElseThrow(() -> new IllegalArgumentException("Patient is not assigned to you."));

        // 3. Remove Relationship (Hard Delete)
        doctorPatientRepo.delete(relation);
        logger.info("Removed patient {} from doctor {}", patientId, doctorEmail);
    }
}
