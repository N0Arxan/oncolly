package cat.teknos.oncolly.dtos.activity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        UUID patientId,
        String activityType,
        String value,
        LocalDateTime occurredAt,
        LocalDateTime createdAt
) {}