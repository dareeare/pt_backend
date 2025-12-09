package medicalcenter.userservice.model.dto.service;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "DTO для чтения данных медицинской услуги")
public record ServiceReadDto(
        @Schema(description = "Уникальный идентификатор услуги", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Название услуги", example = "Консультация кардиолога")
        String nameOfService,

        @Schema(description = "Стоимость услуги", example = "100.00")
        BigDecimal cost,

        @Schema(description = "Продолжительность услуги в минутах", example = "60")
        Integer durationMinutes,

        @Schema(description = "Дополнительная информация об услуге", example = "Первичный прием с осмотром")
        String information,

        @Schema(description = "Идентификатор врача", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID doctorId,

        @Schema(description = "Имя врача", example = "Иван Петров")
        String doctorName
) {
}