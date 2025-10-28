package medicalcenter.userservice.model.dto.doctor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "DTO для чтения данных врача")
public record DoctorReadDto(
        @Schema(description = "Уникальный идентификатор врача", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Фамилия врача", example = "Петров")
        String lastName,

        @Schema(description = "Имя врача", example = "Иван")
        String firstName,

        @Schema(description = "Отчество врача", example = "Сергеевич")
        String middleName,

        @Schema(description = "Специальность врача", example = "Кардиолог")
        String specialty,

        @Schema(description = "Номер телефона", example = "80291234567")
        String phone,

        @Schema(description = "Email адрес", example = "doctor@example.com")
        String email,

        @Schema(description = "Дополнительная информация", example = "Высшая категория, стаж 15 лет")
        String information,

        @Schema(description = "Рейтинг врача", example = "4.8")
        BigDecimal rating
) {
}