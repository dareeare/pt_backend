package medicalcenter.userservice.model.dto.service;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ServiceCreateEditDto(
        @NotEmpty(message = "Service name should not be empty")
        @Size(min = 2, max = 100, message = "Service name should be between 2 and 100 characters")
        String nameOfService,

        @NotNull(message = "Cost should not be null")
        @DecimalMin(value = "0.00", message = "Cost must be positive or zero")
        @Digits(integer = 8, fraction = 2, message = "Cost must have up to 8 integer and 2 fraction digits")
        BigDecimal cost,

        @NotNull(message = "Duration should not be null")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        @Max(value = 480, message = "Duration must be at most 480 minutes (8 hours)")
        Integer durationMinutes,

        String information,

        @NotNull(message = "Doctor ID should not be null")
        UUID doctorId
) {
}