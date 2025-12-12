package cat.teknos.oncolly.dtos.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateActivityRequest(
        @NotNull(message = "ID is required for offline sync")
        UUID id,

        @NotBlank(message = "Activity type is required")
        String activityType,

        @NotBlank(message = "Value cannot be empty")
        String value,

        @NotNull(message = "Occurred time is required")
        LocalDateTime occurredAt
) {}