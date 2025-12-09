package medicalcenter.userservice.model.dto.service;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "DTO для создания или редактирования медицинской услуги")
public record ServiceCreateEditDto(
        @Schema(
                description = "Название услуги",
                example = "Консультация кардиолога",
                minLength = 2,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Service name should not be empty")
        @Size(min = 2, max = 100, message = "Service name should be between 2 and 100 characters")
        String nameOfService,

        @Schema(
                description = "Стоимость услуги",
                example = "100.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Cost should not be null")
        @DecimalMin(value = "0.00", message = "Cost must be positive or zero")
        @Digits(integer = 8, fraction = 2, message = "Cost must have up to 8 integer and 2 fraction digits")
        BigDecimal cost,

        @Schema(
                description = "Продолжительность услуги в минутах",
                example = "60",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Duration should not be null")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 480, message = "Duration must be at most 480 minutes (8 hours)")
        Integer durationMinutes,

        @Schema(
                description = "Дополнительная информация об услуге",
                example = "Первичный прием с осмотром",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String information,

        @Schema(
                description = "Идентификатор врача, предоставляющего услугу",
                example = "123e4567-e89b-12d3-a456-426614174000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId
) {
}