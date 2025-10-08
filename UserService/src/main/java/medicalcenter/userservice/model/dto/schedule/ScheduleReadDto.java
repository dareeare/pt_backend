package medicalcenter.userservice.model.dto.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleReadDto(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDate workDay,
        UUID doctorId,
        String doctorName
) {
}