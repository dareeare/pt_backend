package medicalcenter.userservice.model.dto.operators;

import java.time.LocalDate;
import java.util.UUID;

public record OperatorReadDto(
        UUID id,
        String lastName,
        String firstName,
        String middleName,
        LocalDate dateOfBirth,
        String phone,
        String email
) {
}