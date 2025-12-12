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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

//    @Autowired
//    private DoctorRepository doctorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

//    @PostMapping("/register/doctor")
//    public ResponseEntity<?> registerDoctor(@RequestBody RegisterDoctorRequest request) {
//        if (userRepo.findByEmail(request.email()).isPresent()) {
//            return ResponseEntity.badRequest().body("Email is already taken!");
//        }
//
//        Doctor doctor = new Doctor();
//        doctor.setEmail(request.email());
//        doctor.setSpecialization(request.specialization());
//
//        doctor.setPasswordHash(passwordEncoder.encode(request.password())); // Use password from DTO
//        doctor.setRole(Role.DOCTOR);
//
//        doctorRepo.save(doctor);
//
//        return ResponseEntity.ok("Doctor registered successfully.");
//    }

    // 2. LOGIN (The Main Event)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // A. Find User
        Optional<AppUser> userOpt = userRepo.findByEmail(request.email());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        AppUser user = userOpt.get();

        // B. Check Password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Password");
        }

        // C. Generate Token
        String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        // D. Return Token + Info
        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), user.getId().toString()));
    }
}
