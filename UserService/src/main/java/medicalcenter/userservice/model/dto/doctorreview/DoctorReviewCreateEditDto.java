package medicalcenter.userservice.model.dto.doctorreview;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record DoctorReviewCreateEditDto(
        @NotNull(message = "Patient ID should not be null")
        UUID patientId,

        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId,

        @NotNull(message = "Visit ID should not be null")
        UUID visitId,

        @NotNull(message = "Rating should not be null")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        Integer rating,

        @Size(max = 2000, message = "Comment should not exceed 2000 characters")
        String comment,

        Boolean isApproved
) {
}