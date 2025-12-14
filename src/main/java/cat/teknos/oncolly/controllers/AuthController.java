package cat.teknos.oncolly.controllers;

import cat.teknos.oncolly.dtos.login.AuthResponse;
import cat.teknos.oncolly.dtos.login.LoginRequest;
import cat.teknos.oncolly.dtos.register.RegisterDoctorRequest;
import cat.teknos.oncolly.models.AppUser;
import cat.teknos.oncolly.models.Doctor;
import cat.teknos.oncolly.models.enums.Role;
import cat.teknos.oncolly.repositories.DoctorRepository;
import cat.teknos.oncolly.repositories.UserRepository;
import cat.teknos.oncolly.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepo;

//    @Autowired
//    private DoctorRepository doctorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // 2. LOGIN (The Main Event)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Login attempt for email: {}", request.email());

        // A. Find User
        Optional<AppUser> userOpt = userRepo.findByEmail(request.email());
        if (userOpt.isEmpty()) {
            logger.warn("Login failed: User not found for email: {}", request.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        AppUser user = userOpt.get();

        // B. Check Password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            logger.warn("Login failed: Invalid password for email: {}", request.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Password");
        }

        // C. Generate Token
        String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        logger.info("Login successful for user: {}", user.getId());
        // D. Return Token + Info
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), user.getId().toString()));
    }
}
