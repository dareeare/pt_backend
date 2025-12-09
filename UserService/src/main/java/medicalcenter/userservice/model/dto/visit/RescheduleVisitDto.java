package medicalcenter.userservice.model.dto.visit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для запроса переноса записи на другое время/дату
 */
public record RescheduleVisitDto(
        @NotNull(message = "New visit date should not be null")
        @Future(message = "New visit date should be in the future")
        LocalDateTime newDateOfVisit,

        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId,

        // ID слота времени, если нужно использовать конкретный слот
        // Если не указан (null), будет найден слот по doctorId и newDateOfVisit
        UUID timeSlotId
) {
}

