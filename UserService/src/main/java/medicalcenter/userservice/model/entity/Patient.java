package medicalcenter.userservice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Сущность представляет пациента медицинского центра.
 * Содержит демографические данные: ФИО, контакты, дата рождения, пол.
 * Связана с историей визитов через коллекцию visits.
 * CHECK-constraint ограничивает значения поля gender ('M', 'F', 'O').
 *
 * Пример: "Пациент: Петров Иван Сергеевич, 01.01.1980, M"
 */
@Schema(description = "Сущность пациента медицинского центра")
@Data
@NoArgsConstructor
@ToString(exclude = "visits")
@EqualsAndHashCode(exclude = "visits")
@Entity
@Table(name = "Patient")
public class Patient {

    @Schema(description = "Уникальный идентификатор пациента", example = "123e4567-e89b-12d3-a456-426614174000")
    @Id
    @Column(name = "patient_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Schema(description = "Имя пациента", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Schema(description = "Фамилия пациента", example = "Петров", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Schema(description = "Отчество пациента", example = "Сергеевич")
    @Column(name = "middle_name")
    private String middleName;

    @Schema(description = "Номер телефона в формате Беларуси", example = "80291234567")
    @Column(name = "phone")
    private String phone;

    @Schema(description = "Email адрес", example = "ivan.petrov@example.com")
    @Column(name = "email")
    private String email;

    @Schema(description = "Дата рождения", example = "1990-01-01")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Schema(description = "Пол пациента", example = "M", allowableValues = {"M", "F", "O"})
    @Column(name = "gender")
    private String gender;

    @Schema(description = "Список визитов пациента")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    @Schema(description = "Путь к файлу аватарки пациента", example = "/avatars/patient-123e4567-e89b-12d3-a456-426614174000.jpg")
    @Column(name = "avatar_path")
    private String avatarPath;

    @Builder
    public Patient(String firstName, String lastName, String middleName,
                   String phone, String email, String gender, LocalDate dateOfBirth,
                   List<Visit> visits, String avatarPath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.visits = visits;
        this.avatarPath = avatarPath;
    }
}