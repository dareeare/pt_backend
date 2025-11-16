package medicalcenter.userservice.model.dto.operator;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO для чтения данных оператора")
public record OperatorReadDto(
        @Schema(description = "Уникальный идентификатор оператора", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Фамилия оператора", example = "Сидорова")
        String lastName,

        @Schema(description = "Имя оператора", example = "Анна")
        String firstName,

        @Schema(description = "Отчество оператора", example = "Ивановна")
        String middleName,

        @Schema(description = "Дата рождения", example = "1990-08-20")
        LocalDate dateOfBirth,

        @Schema(description = "Номер телефона", example = "80291234567")
        String phone,

        @Schema(description = "Email адрес", example = "operator@example.com")
        String email,

        @Schema(description = "Путь к аватарке оператора", example = "/avatars/operator-123e4567-e89b-12d3-a456-426614174000.jpg")
        String avatarPath
) {
}