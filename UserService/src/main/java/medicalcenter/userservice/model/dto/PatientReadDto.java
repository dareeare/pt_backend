package medicalcenter.userservice.model.dto;

import java.time.LocalDate;

public record PatientReadDto(
        String lastName,
        String firstName,
        LocalDate birthDate
) {
}
