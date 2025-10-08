package medicalcenter.userservice.model.dto.schedule;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleCreateEditDto(
        @NotNull(message = "Start time should not be null")
        LocalDateTime startTime,

        @NotNull(message = "End time should not be null")
        LocalDateTime endTime,

        @NotNull(message = "Work day should not be null")
        LocalDate workDay,

        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId
) {
}