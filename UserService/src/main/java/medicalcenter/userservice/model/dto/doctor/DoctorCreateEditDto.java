package medicalcenter.userservice.model.dto.doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "DTO для создания или редактирования врача")
public record DoctorCreateEditDto(
        @Schema(
                description = "Имя врача",
                example = "Иван",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @Schema(
                description = "Фамилия врача",
                example = "Петров",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Last name should not be empty")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Schema(
                description = "Отчество врача",
                example = "Сергеевич",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
        String middleName,

        @Schema(
                description = "Специальность врача",
                example = "Кардиолог",
                minLength = 2,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotEmpty(message = "Specialty should not be empty")
        @Size(min = 2, max = 100, message = "Specialty should be between 2 and 100 characters")
        String specialty,

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
                example = "doctor@example.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Email(message = "Email should be valid")
        String email,

        @Schema(
                description = "Дополнительная информация о враче",
                example = "Высшая категория, стаж 15 лет",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String information,

        @Schema(
                description = "Рейтинг врача",
                example = "4.8",
                minimum = "0.00",
                maximum = "5.00",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @DecimalMin(value = "0.00", message = "Rating must be at least 0.00")
        @DecimalMax(value = "5.00", message = "Rating must be at most 5.00")
        BigDecimal rating,

        @Schema(
                description = "Путь к аватарке врача",
                example = "/avatars/doctor-123e4567-e89b-12d3-a456-426614174000.jpg",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
                String avatarPath
) {
}