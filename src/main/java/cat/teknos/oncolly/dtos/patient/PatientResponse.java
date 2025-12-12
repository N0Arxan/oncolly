package cat.teknos.oncolly.dtos.patient;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
        UUID id,
        String email,
        String phoneNumber,
        LocalDate dateOfBirth
) {}