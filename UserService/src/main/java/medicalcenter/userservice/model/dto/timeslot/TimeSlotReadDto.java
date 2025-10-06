package medicalcenter.userservice.model.dto.timeslot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TimeSlotReadDto(
        UUID id,
        UUID doctorId,
        String doctorName,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime,
        UUID visitId,
        String patientName,
        Boolean isAvailable
) {
}