package cat.teknos.oncolly.config;

import cat.teknos.oncolly.models.*;
import cat.teknos.oncolly.models.enums.AppointmentStatus;
import cat.teknos.oncolly.models.enums.Role;
import cat.teknos.oncolly.models.keys.DoctorPatientKey;
import cat.teknos.oncolly.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final DoctorPatientRepository doctorPatientRepo;
    private final ActivityRepository activityRepo;
    private final AppointmentRepository appointmentRepo;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection (Cleaner than @Autowired)
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
            System.out.println("✅ Data already exists. Skipping seeding.");
            return;
        }

        System.out.println("🌱 Seeding Database...");

        // ==========================================
        // 2. Create DOCTOR ("Dr. House")
        // ==========================================
        Doctor doctor = new Doctor();
        doctor.setEmail("house@hospital.com");
        doctor.setPasswordHash(passwordEncoder.encode("doctor123")); // Hash the password!
        doctor.setRole(Role.DOCTOR);
        doctor.setSpecialization("Diagnostic Medicine");

        Doctor savedDoctor = doctorRepo.save(doctor);

        // ==========================================
        // 3. Create PATIENT ("John Doe")
        // ==========================================
        Patient patient = new Patient();
        patient.setEmail("john@patient.com");
        patient.setPasswordHash(passwordEncoder.encode("patient123"));
        patient.setRole(Role.PATIENT);
        patient.setPhoneNumber("+1-555-0199");
        patient.setDateOfBirth(LocalDate.of(1985, 5, 20));

        Patient savedPatient = patientRepo.save(patient);

        // ==========================================
        // 4. LINK THEM (Doctor treats Patient)
        // ==========================================
        DoctorPatient link = new DoctorPatient();
        link.setId(new DoctorPatientKey(savedDoctor.getId(), savedPatient.getId()));
        link.setDoctor(savedDoctor);
        link.setPatient(savedPatient);

        doctorPatientRepo.save(link);

        // ==========================================
        // 5. Create ACTIVITIES (History)
        // ==========================================
        Activity act1 = new Activity();
        act1.setId(UUID.randomUUID());
        act1.setPatient(savedPatient);
        act1.setActivityType("Blood Pressure");
        act1.setValue("120/80");
        act1.setOccurredAt(LocalDateTime.now().minusDays(2)); // 2 days ago

        Activity act2 = new Activity();
        act2.setId(UUID.randomUUID());
        act2.setPatient(savedPatient);
        act2.setActivityType("Running");
        act2.setValue("5km in 30min");
        act2.setOccurredAt(LocalDateTime.now().minusDays(1)); // Yesterday

        activityRepo.save(act1);
        activityRepo.save(act2);

        // ==========================================
        // 6. Create APPOINTMENT
        // ==========================================
        Appointment appt = new Appointment();
        appt.setId(UUID.randomUUID());
        appt.setDoctor(savedDoctor);
        appt.setPatient(savedPatient);
        appt.setStartTime(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0)); // 3 days from now, 10:00 AM
        appt.setEndTime(LocalDateTime.now().plusDays(3).withHour(11).withMinute(0));
        appt.setTitle("Routine Checkup");
        appt.setNotes("Check blood pressure again.");
        appt.setStatus(AppointmentStatus.CONFIRMED);

        appointmentRepo.save(appt);

        // ==========================================
        // 7. Log Credentials for easy access
        // ==========================================
        System.out.println("✅ Database Seeded Successfully!");
        System.out.println("---------------------------------");
        System.out.println("👨‍⚕️ Doctor Login: house@hospital.com / doctor123");
        System.out.println("🤒 Patient Login: john@patient.com / patient123");
        System.out.println("---------------------------------");
    }
}