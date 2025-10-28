package medicalcenter.userservice.model.dto.visit;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "DTO для чтения данных визита")
public record VisitReadDto(
        @Schema(description = "Уникальный идентификатор визита", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Дата и время визита", example = "2024-01-15T10:00:00")
        LocalDateTime dateOfVisit,

        @Schema(description = "Идентификатор врача", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID doctorId,

        @Schema(description = "Имя врача", example = "Иван Петров")
        String doctorName,

        @Schema(description = "Идентификатор пациента", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID patientId,

        @Schema(description = "Имя пациента", example = "Сергей Иванов")
        String patientName,

        @Schema(description = "Статус визита", example = "scheduled", allowableValues = {"scheduled", "completed", "cancelled"})
        String status,

        @Schema(description = "Симптомы пациента", example = "Головная боль, повышенное давление")
        String symptoms,

        @Schema(description = "Диагноз", example = "Артериальная гипертензия")
        String diagnosis,

        @Schema(description = "Назначения врача", example = "Принимать препарат X по 1 таблетке 2 раза в день")
        String prescription
) {
}