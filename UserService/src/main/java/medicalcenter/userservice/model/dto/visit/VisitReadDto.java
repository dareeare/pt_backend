package medicalcenter.userservice.model.dto.visit;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitReadDto(
        UUID id,
        LocalDateTime dateOfVisit,
        UUID doctorId,
        String doctorName,
        UUID patientId,
        String patientName,
        String status,
        String symptoms,
        String diagnosis,
        String prescription
) {
}