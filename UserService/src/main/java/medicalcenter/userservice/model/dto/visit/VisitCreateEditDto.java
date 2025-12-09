package medicalcenter.userservice.model.dto.visit;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "DTO для создания или редактирования визита")
public record VisitCreateEditDto(
        @Schema(
                description = "Дата и время визита",
                example = "2024-01-15T10:00:00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Visit date should not be null")
        @Future(message = "Visit date should be in the future")
        LocalDateTime dateOfVisit,

        @Schema(
                description = "Идентификатор врача",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId,

        @Schema(
                description = "Идентификатор пациента",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Patient ID should not be null")
        UUID patientId,

        @Schema(
                description = "Статус визита",
                example = "scheduled",
                allowableValues = {"scheduled", "completed", "cancelled"},
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Pattern(regexp = "^(scheduled|completed|cancelled)$",
                message = "Status must be one of: scheduled, completed, cancelled")
        String status,

        @Schema(
                description = "Симптомы пациента",
                example = "Головная боль, повышенное давление",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Symptoms should not exceed 1000 characters")
        String symptoms,

        @Schema(
                description = "Диагноз",
                example = "Артериальная гипертензия",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Diagnosis should not exceed 1000 characters")
        String diagnosis,

        @Schema(
                description = "Назначения врача",
                example = "Принимать препарат X по 1 таблетке 2 раза в день",
                maxLength = 1000,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 1000, message = "Prescription should not exceed 1000 characters")
        String prescription
) {
}