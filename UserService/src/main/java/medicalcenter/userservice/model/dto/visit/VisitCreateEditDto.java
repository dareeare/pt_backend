package medicalcenter.userservice.model.dto.visit;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record VisitCreateEditDto(
        @NotNull(message = "Visit date should not be null")
        @Future(message = "Visit date should be in the future")
        LocalDateTime dateOfVisit,

        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId,

        @NotNull(message = "Patient ID should not be null")
        UUID patientId,

        @Pattern(regexp = "^(scheduled|completed|cancelled)$",
                message = "Status must be one of: scheduled, completed, cancelled")
        String status,

        @Size(max = 1000, message = "Symptoms should not exceed 1000 characters")
        String symptoms,

        @Size(max = 1000, message = "Diagnosis should not exceed 1000 characters")
        String diagnosis,

        @Size(max = 1000, message = "Prescription should not exceed 1000 characters")
        String prescription
) {
}