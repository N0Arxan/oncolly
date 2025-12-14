package cat.teknos.oncolly.config;

import cat.teknos.oncolly.models.*;
import cat.teknos.oncolly.models.enums.AppointmentStatus;
import cat.teknos.oncolly.models.enums.Role;
import cat.teknos.oncolly.models.keys.DoctorPatientKey;
import cat.teknos.oncolly.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final DoctorPatientRepository doctorPatientRepo;
    private final ActivityRepository activityRepo;
    private final AppointmentRepository appointmentRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(DoctorRepository doctorRepo,
                      PatientRepository patientRepo,
                      DoctorPatientRepository doctorPatientRepo,
                      ActivityRepository activityRepo,
                      AppointmentRepository appointmentRepo,
                      PasswordEncoder passwordEncoder) {
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.doctorPatientRepo = doctorPatientRepo;
        this.activityRepo = activityRepo;
        this.appointmentRepo = appointmentRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (doctorRepo.count() > 0) {
            logger.info("✅ Data already exists. Skipping seeding.");
            return;
        }

        logger.info("🌱 Seeding Database...");

        // ==========================================
        // 1. Create DOCTOR ("Dr. House")
        // ==========================================
        Doctor doctor = new Doctor();
        doctor.setFirstName("Gregory");
        doctor.setLastName("House");
        doctor.setEmail("house@hospital.com");
        doctor.setPasswordHash(passwordEncoder.encode("doctor123"));
        doctor.setRole(Role.DOCTOR);
        doctor.setSpecialization("Diagnostic Medicine");

        Doctor savedDoctor = doctorRepo.save(doctor);

        // ==========================================
        // 2. Create PATIENT 1 ("John Doe")
        // ==========================================
        Patient p1 = createPatient("John", "Doe", "john@patient.com", "+1-555-0101", LocalDate.of(1985, 5, 20));
        linkPatientToDoctor(savedDoctor, p1);
        createSampleDataForPatient(p1, savedDoctor);

        // ==========================================
        // 3. Create PATIENT 2 ("Jane Smith")
        // ==========================================
        Patient p2 = createPatient("Jane", "Smith", "jane@patient.com", "+1-555-0202", LocalDate.of(1990, 8, 15));
        linkPatientToDoctor(savedDoctor, p2);
        createSampleDataForPatient(p2, savedDoctor);

        // ==========================================
        // 4. Create PATIENT 3 ("Robert Brown")
        // ==========================================
        Patient p3 = createPatient("Robert", "Brown", "bob@patient.com", "+1-555-0303", LocalDate.of(1978, 11, 30));
        linkPatientToDoctor(savedDoctor, p3);
        // Bob is new, no data yet.

        // ==========================================
        // 5. Log Credentials
        // ==========================================
        logger.info("✅ Database Seeded Successfully!");
        logger.info("---------------------------------");
        logger.info("👨‍⚕️ Doctor Login: house@hospital.com / doctor123");
        logger.info("🤒 Patient 1:    john@patient.com   / patient123");
        logger.info("🤒 Patient 2:    jane@patient.com   / patient123");
        logger.info("🤒 Patient 3:    bob@patient.com    / patient123");
        logger.info("---------------------------------");
    }

    private Patient createPatient(String first, String last, String email, String phone, LocalDate dob) {
        Patient p = new Patient();
        p.setFirstName(first);
        p.setLastName(last);
        p.setEmail(email);
        p.setPasswordHash(passwordEncoder.encode("patient123"));
        p.setRole(Role.PATIENT);
        p.setPhoneNumber(phone);
        p.setDateOfBirth(dob);
        return patientRepo.save(p);
    }

    private void linkPatientToDoctor(Doctor doc, Patient pat) {
        DoctorPatient link = new DoctorPatient();
        link.setId(new DoctorPatientKey(doc.getId(), pat.getId()));
        link.setDoctor(doc);
        link.setPatient(pat);
        doctorPatientRepo.save(link);
    }

    private void createSampleDataForPatient(Patient patient, Doctor doctor) {
        // Activity 1
        Activity act1 = new Activity();
        act1.setId(UUID.randomUUID());
        act1.setPatient(patient);
        act1.setActivityType("Heart Rate");
        act1.setValue("72 bpm");
        act1.setOccurredAt(LocalDateTime.now().minusDays(2));
        activityRepo.save(act1);

        // Activity 2
        Activity act2 = new Activity();
        act2.setId(UUID.randomUUID());
        act2.setPatient(patient);
        act2.setActivityType("Steps");
        act2.setValue("10500 steps");
        act2.setOccurredAt(LocalDateTime.now().minusDays(1));
        activityRepo.save(act2);

        // Appointment
        Appointment appt = new Appointment();
        appt.setId(UUID.randomUUID());
        appt.setDoctor(doctor);
        appt.setPatient(patient);
        appt.setStartTime(LocalDateTime.now().plusDays(5).withHour(14).withMinute(0));
        appt.setEndTime(LocalDateTime.now().plusDays(5).withHour(14).withMinute(30));
        appt.setTitle("Follow-up");
        appt.setNotes("Review activity logs.");
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepo.save(appt);
    }
}