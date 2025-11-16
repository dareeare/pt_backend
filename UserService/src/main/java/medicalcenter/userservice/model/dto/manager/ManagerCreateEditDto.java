package medicalcenter.userservice.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "DTO для создания или редактирования менеджера")
public record ManagerCreateEditDto(
        @Schema(
                description = "Имя менеджера",
                example = "Иван",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @Schema(
                description = "Фамилия менеджера",
                example = "Петров",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Last name should not be empty")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Schema(
                description = "Отчество менеджера",
                example = "Сергеевич",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
        String middleName,

        @Schema(
                description = "Дата рождения",
                example = "1985-05-15",
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
                example = "manager@example.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Email(message = "Email should be valid")
        String email,

        @Schema(
                description = "Путь к аватарке менеджера",
                example = "/avatars/manager-123e4567-e89b-12d3-a456-426614174000.jpg",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String avatarPath
) {
}