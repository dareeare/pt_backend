package medicalcenter.userservice.model.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO для чтения данных пациента")
public record PatientReadDto(
        @Schema(description = "Уникальный идентификатор пациента", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Фамилия пациента", example = "Петров")
        String lastName,

        @Schema(description = "Имя пациента", example = "Иван")
        String firstName,

        @Schema(description = "Отчество пациента", example = "Сергеевич")
        String middleName,

        @Schema(description = "Номер телефона", example = "80291234567")
        String phone,

        @Schema(description = "Email адрес", example = "ivan.petrov@example.com")
        String email,

        @Schema(description = "Дата рождения", example = "1990-01-01")
        LocalDate dateOfBirth,

        @Schema(description = "Пол пациента", example = "M", allowableValues = {"M", "F", "O"})
        String gender
) {
}