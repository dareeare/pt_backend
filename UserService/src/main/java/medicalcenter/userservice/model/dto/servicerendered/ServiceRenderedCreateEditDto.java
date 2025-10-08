package medicalcenter.userservice.model.dto.servicerendered;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record ServiceRenderedCreateEditDto(
        @NotNull(message = "Visit ID should not be null")
        UUID visitId,

        @NotNull(message = "Service ID should not be null")
        UUID serviceId,

        @NotNull(message = "Actual cost should not be null")
        @DecimalMin(value = "0.00", message = "Actual cost must be positive or zero")
        @Digits(integer = 8, fraction = 2, message = "Actual cost must have up to 8 integer and 2 fraction digits")
        BigDecimal actualCost
) {
}