package medicalcenter.userservice.model.dto.operator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "DTO для создания или редактирования оператора")
public record OperatorCreateEditDto(
        @Schema(
                description = "Имя оператора",
                example = "Анна",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @Schema(
                description = "Фамилия оператора",
                example = "Сидорова",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Last name should not be empty")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Schema(
                description = "Отчество оператора",
                example = "Ивановна",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
        String middleName,

        @Schema(
                description = "Дата рождения",
                example = "1990-08-20",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Date of birth should not be null")
        @Past(message = "Date of birth should be in the past")
        LocalDate dateOfBirth,

        @Schema(
                description = "Номер телефона в формате Беларуси",
                example = "80291234567",
                pattern = "^80(29|17|33|44|25)\\d{7}$",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Phone should not be empty")
        @Pattern(regexp = "^80(29|17|33|44|25)\\d{7}$", message = "Phone must have format 80(29|17|33|44|25) followed by 7 digits")
        String phone,

        @Schema(
                description = "Email адрес",
                example = "operator@example.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Email(message = "Email should be valid")
        String email,

        @Schema(
                description = "Путь к аватарке оператора",
                example = "/avatars/operator-123e4567-e89b-12d3-a456-426614174000.jpg",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String avatarPath
) {
}