package medicalcenter.userservice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность представляет сотрудника call-центра медицинского центра.
 * Операторы отвечают за прием звонков, запись пациентов и консультации.
 *
 * Пример: "Оператор Сидорова А.И., телефон: +375298759356"
 */
@Schema(description = "Сущность оператора call-центра медицинского центра")
@Data
@NoArgsConstructor
@Entity
@Table(name = "operators")
public class Operator {

    @Schema(description = "Уникальный идентификатор оператора", example = "123e4567-e89b-12d3-a456-426614174000")
    @Id
    @Column(name = "operator_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Schema(description = "Имя оператора", example = "Анна", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Schema(description = "Фамилия оператора", example = "Сидорова", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Schema(description = "Отчество оператора", example = "Ивановна")
    @Column(name = "middle_name")
    private String middleName;

    @Schema(description = "Дата рождения", example = "1990-08-20", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Schema(description = "Номер телефона в формате Беларуси", example = "80291234567", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "phone", nullable = false)
    private String phone;

    @Schema(description = "Email адрес", example = "operator@example.com")
    @Column(name = "email")
    private String email;

    @Builder
    public Operator(String firstName, String lastName, String middleName,
                    LocalDate dateOfBirth, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
    }
}