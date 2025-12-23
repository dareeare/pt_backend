package medicalcenter.userservice.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "DTO для обновления профиля пациента менеджером")
public record PatientUpdateDto(
        @Schema(
                description = "Имя пациента",
                example = "Иван",
                minLength = 2,
                maxLength = 50
        )
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @Schema(
                description = "Фамилия пациента",
                example = "Петров",
                minLength = 2,
                maxLength = 50
        )
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Schema(
                description = "Номер телефона в формате Беларуси",
                example = "80291234567",
                pattern = "^80(29|17|33|44|25)\\d{7}$"
        )
        @Pattern(regexp = "^80(29|17|33|44|25)\\d{7}$", message = "Phone must have format 80(29|17|33|44|25) followed by 7 digits")
        String phone,

        @Schema(
                description = "Дата рождения",
                example = "1990-01-01"
        )
        LocalDate dateOfBirth
) {
}
