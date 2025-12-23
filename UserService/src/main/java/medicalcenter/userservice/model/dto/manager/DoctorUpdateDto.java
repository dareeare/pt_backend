package medicalcenter.userservice.model.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "DTO для обновления профиля врача менеджером")
public record DoctorUpdateDto(
        @Schema(
                description = "Имя врача",
                example = "Иван",
                minLength = 2,
                maxLength = 50
        )
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @Schema(
                description = "Фамилия врача",
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
                example = "1980-05-15"
        )
        LocalDate dateOfBirth,

        @Schema(
                description = "Специальность врача",
                example = "Кардиолог",
                minLength = 2,
                maxLength = 100
        )
        @Size(min = 2, max = 100, message = "Specialty should be between 2 and 100 characters")
        String specialty
) {
}
