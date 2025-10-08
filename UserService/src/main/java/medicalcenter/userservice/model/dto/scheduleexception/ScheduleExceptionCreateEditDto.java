package medicalcenter.userservice.model.dto.scheduleexception;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduleExceptionCreateEditDto(
        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId,

        @NotNull(message = "Exception date should not be null")
        @Future(message = "Exception date should be in the future")
        LocalDate exceptionDate,

        @Size(max = 255, message = "Reason should not exceed 255 characters")
        String reason,

        Boolean isWorkingDay
) {
}