package medicalcenter.userservice.model.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "DTO для обновления профиля пользователя (без обязательных полей gender)")
public record ProfileUpdateDto(
        @Schema(
                description = "Имя пациента",
                example = "Иван",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @Schema(
                description = "Фамилия пациента",
                example = "Петров",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Last name should not be empty")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Schema(
                description = "Номер телефона в формате Беларуси",
                example = "80291234567",
                pattern = "^80(29|17|33|44|25)\\d{7}$",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String phone,

        @Schema(
                description = "Дата рождения",
                example = "1990-01-01",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Past(message = "Date of birth should be in the past")
        LocalDate birthDate
) {
}

