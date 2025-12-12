package cat.teknos.oncolly.dtos.appointment;

import cat.teknos.oncolly.models.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID doctorId,
        String doctorName,
        UUID patientId,
        String patientName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status,
        String title
) {}