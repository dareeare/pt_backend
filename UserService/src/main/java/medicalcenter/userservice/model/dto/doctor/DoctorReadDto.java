package medicalcenter.userservice.model.dto.doctor;

import java.math.BigDecimal;
import java.util.UUID;

public record DoctorReadDto(
        UUID id,
        String lastName,
        String firstName,
        String middleName,
        String specialty,
        String phone,
        String email,
        String information,
        BigDecimal rating
) {
}
