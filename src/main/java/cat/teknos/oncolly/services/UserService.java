package cat.teknos.oncolly.services;

import cat.teknos.oncolly.dtos.user.UpdateProfileRequest;
import cat.teknos.oncolly.models.AppUser;
import cat.teknos.oncolly.models.Doctor;
import cat.teknos.oncolly.models.Patient;
import cat.teknos.oncolly.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser updateProfile(String email, UpdateProfileRequest request) {
        logger.info("Updating profile for user: {}", email);

        AppUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 1. Update Common Fields
        if (request.firstName() != null && !request.firstName().isBlank()) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            user.setLastName(request.lastName());
        }
        if (request.email() != null && !request.email().isBlank()) {
            // Check if email is being changed and if it's already taken
            if (!request.email().equals(user.getEmail()) && userRepo.findByEmail(request.email()).isPresent()) {
                throw new IllegalArgumentException("Email already taken");
            }
            user.setEmail(request.email());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        // 2. Update Patient Specifics
        if (user instanceof Patient patient) {
            if (request.phoneNumber() != null) {
                patient.setPhoneNumber(request.phoneNumber());
            }
            if (request.dateOfBirth() != null) {
                patient.setDateOfBirth(request.dateOfBirth());
            }
        }

        // 3. Update Doctor Specifics
        if (user instanceof Doctor doctor) {
            if (request.specialization() != null && !request.specialization().isBlank()) {
                doctor.setSpecialization(request.specialization());
            }
        }

        AppUser savedUser = userRepo.save(user);
        logger.info("Profile updated successfully for user: {}", savedUser.getId());
        return savedUser;
    }
}
