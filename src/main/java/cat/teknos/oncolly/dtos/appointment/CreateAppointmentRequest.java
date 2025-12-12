package cat.teknos.oncolly.dtos.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull(message = "Appointment ID is required")
        UUID id,

        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        LocalDateTime endTime,

        @NotBlank(message = "Title is required")
        String title,

        String doctorNotes
) {}
