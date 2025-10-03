package medicalcenter.userservice.model.dto.patient;

import java.time.LocalDate;
import java.util.UUID;

public record PatientReadDto(
        UUID id,
        String lastName,
        String firstName,
        String middleName,
        String phone,
        String email,
        LocalDate dateOfBirth,
        String gender
) {
}
