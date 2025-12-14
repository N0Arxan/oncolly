package cat.teknos.oncolly.dtos.user;

import java.time.LocalDate;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,     // Patient only
        LocalDate dateOfBirth,  // Patient only
        String specialization   // Doctor only
) {}
