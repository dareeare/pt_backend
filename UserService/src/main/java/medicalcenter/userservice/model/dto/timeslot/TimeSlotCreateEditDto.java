package medicalcenter.userservice.model.dto.timeslot;

import jakarta.validation.constraints.*;
import medicalcenter.userservice.model.entity.Visit;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TimeSlotCreateEditDto(
        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId,

        @NotNull(message = "Slot date should not be null")
        @FutureOrPresent(message = "Slot date should be in the future or present")
        LocalDate slotDate,

        @NotNull(message = "Start time should not be null")
        LocalTime startTime,

        @NotNull(message = "End time should not be null")
        LocalTime endTime,

        @NotNull(message = "Visit should not be null")
        Visit visit
) {
}