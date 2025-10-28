package medicalcenter.userservice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Основная сущность, представляющая врача медицинского центра.
 * Содержит личную информацию, специализацию, контакты и рейтинг.
 * Связана с сущностями: визиты, услуги, расписание, исключения в расписании, отзывы.
 * Рейтинг автоматически рассчитывается на основе одобренных отзывов.
 *
 * Пример: "Кардиолог Иванов А.Б., рейтинг 4.8, телефон: +79997654321"
 */
@Schema(description = "Сущность врача медицинского центра")
@Data
@NoArgsConstructor
@ToString(exclude = {"visits", "services", "schedules", "reviews"})
@EqualsAndHashCode(exclude = {"visits", "services", "schedules", "reviews"})
@Entity
@Table(name = "Doctor")
public class Doctor {

    @Schema(description = "Уникальный идентификатор врача", example = "123e4567-e89b-12d3-a456-426614174000")
    @Id
    @Column(name = "doctor_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Schema(description = "Имя врача", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Schema(description = "Фамилия врача", example = "Петров", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Schema(description = "Отчество врача", example = "Сергеевич")
    @Column(name = "middle_name")
    private String middleName;

    @Schema(description = "Специальность врача", example = "Кардиолог", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "specialty", nullable = false)
    private String specialty;

    @Schema(description = "Номер телефона в формате Беларуси", example = "80291234567", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "phone", nullable = false)
    private String phone;

    @Schema(description = "Email адрес", example = "doctor@example.com")
    @Column(name = "email")
    private String email;

    @Schema(description = "Дополнительная информация о враче", example = "Высшая категория, стаж 15 лет")
    @Column(name = "information")
    private String information;

    @Schema(description = "Рейтинг врача", example = "4.8")
    @Column(name = "rating")
    private BigDecimal rating;

    @Schema(description = "Список визитов врача")
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    @Schema(description = "Список услуг врача")
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services;

    @Schema(description = "Расписание врача")
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    @Schema(description = "Отзывы о враче")
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorReview> reviews;

    @Builder
    public Doctor(String firstName, String lastName, String middleName,
                  String specialty, String phone, String email,
                  String information) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
        this.information = information;
        this.rating = BigDecimal.ZERO;
        this.visits = new ArrayList<>();
        this.services = new ArrayList<>();
        this.schedules = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }
}