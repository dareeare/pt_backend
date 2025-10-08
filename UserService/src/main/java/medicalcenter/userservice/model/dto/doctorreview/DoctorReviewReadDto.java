package medicalcenter.userservice.model.dto.doctorreview;

import java.time.LocalDateTime;
import java.util.UUID;

public record DoctorReviewReadDto(
        UUID id,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        UUID visitId,
        LocalDateTime visitDate,
        Integer rating,
        String comment,
        Boolean isApproved,
        Boolean isEdited,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}