package medicalcenter.userservice.model.dto.scheduleexceptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleExceptionReadDto(
        UUID id,
        UUID doctorId,
        String doctorName,
        LocalDate exceptionDate,
        String reason,
        Boolean isWorkingDay,
        LocalDateTime createdAt
) {
}