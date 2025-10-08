package medicalcenter.userservice.model.dto.service;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceReadDto(
        UUID id,
        String nameOfService,
        BigDecimal cost,
        Integer durationMinutes,
        String information,
        UUID doctorId,
        String doctorName
) {
}