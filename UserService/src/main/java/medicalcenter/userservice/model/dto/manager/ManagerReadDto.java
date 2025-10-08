package medicalcenter.userservice.model.dto.manager;

import java.time.LocalDate;
import java.util.UUID;

public record ManagerReadDto(
        UUID id,
        String lastName,
        String firstName,
        String middleName,
        LocalDate dateOfBirth,
        String phone,
        String email
) {
}