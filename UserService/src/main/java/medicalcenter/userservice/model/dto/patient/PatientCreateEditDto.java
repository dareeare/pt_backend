package medicalcenter.userservice.model.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "DTO для создания или редактирования пациента")
public record PatientCreateEditDto(
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
                description = "Отчество пациента",
                example = "Сергеевич",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
        String middleName,

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
                example = "ivan.petrov@example.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Email
        String email,

        @Schema(
                description = "Дата рождения",
                example = "1990-01-01",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Past(message = "Date of birth should be in the past")
        LocalDate dateOfBirth,

        @Schema(
                description = "Пол пациента",
                example = "M",
                allowableValues = {"M", "F", "O"},
                maxLength = 1,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Gender should not be empty")
        @Size(max = 1, message = "Gender consists of 1 character")
        @Pattern(regexp = "^[MFO]$", message = "Gender must be one of 'M', 'F', or 'O'")
        String gender
) {
}