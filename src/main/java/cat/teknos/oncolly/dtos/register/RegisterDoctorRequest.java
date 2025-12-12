package cat.teknos.oncolly.dtos.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDoctorRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String specialization,
        String clinicName // Optional
) {}