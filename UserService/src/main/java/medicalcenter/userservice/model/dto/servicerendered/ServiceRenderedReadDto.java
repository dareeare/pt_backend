package medicalcenter.userservice.model.dto.servicerendered;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceRenderedReadDto(
        UUID id,
        UUID visitId,
        LocalDateTime visitDate,
        UUID serviceId,
        String serviceName,
        BigDecimal actualCost,
        BigDecimal standardCost
) {
}