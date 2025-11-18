package medicalcenter.userservice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность представляет менеджера медицинского центра.
 * Менеджеры отвечают за административные функции, управление персоналом и процессами.
 * Менеджер может добавлять/удалять врачей, корректировать информацию врачей,
 * добавлять/удалять/редактировать услуги
 */
@Schema(description = "Сущность менеджера медицинского центра")
@Data
@NoArgsConstructor
@Entity
@Table(name = "manager")
public class Manager {

    @Schema(description = "Уникальный идентификатор менеджера", example = "123e4567-e89b-12d3-a456-426614174000")
    @Id
    @Column(name = "manager_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Schema(description = "Имя менеджера", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Schema(description = "Фамилия менеджера", example = "Петров", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Schema(description = "Отчество менеджера", example = "Сергеевич")
    @Column(name = "middle_name")
    private String middleName;

    @Schema(description = "Дата рождения", example = "1985-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Schema(description = "Номер телефона в формате Беларуси", example = "80291234567", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "phone", nullable = false)
    private String phone;

    @Schema(description = "Email адрес", example = "manager@example.com")
    @Column(name = "email")
    private String email;

    @Schema(description = "Путь к файлу аватарки менеджера", example = "/avatars/manager-123e4567-e89b-12d3-a456-426614174000.jpg")
    @Column(name = "avatar_path")
    private String avatarPath;

    @Builder
    public Manager(String firstName, String lastName, String middleName,
                   LocalDate dateOfBirth, String phone, String email, String avatarPath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.avatarPath = avatarPath;
    }
}