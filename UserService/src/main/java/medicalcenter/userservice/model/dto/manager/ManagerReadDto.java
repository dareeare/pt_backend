package medicalcenter.userservice.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO для чтения данных менеджера")
public record ManagerReadDto(
        @Schema(description = "Уникальный идентификатор менеджера", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Фамилия менеджера", example = "Петров")
        String lastName,

        @Schema(description = "Имя менеджера", example = "Иван")
        String firstName,

        @Schema(description = "Отчество менеджера", example = "Сергеевич")
        String middleName,

        @Schema(description = "Дата рождения", example = "1985-05-15")
        LocalDate dateOfBirth,

        @Schema(description = "Номер телефона", example = "80291234567")
        String phone,

        @Schema(description = "Email адрес", example = "manager@example.com")
        String email
) {
}