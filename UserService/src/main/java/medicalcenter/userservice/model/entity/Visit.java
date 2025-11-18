package medicalcenter.userservice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность представляет визит пациента к врачу.
 * Содержит информацию о дате визита, симптомах, диагнозе и назначениях.
 * Связана с сущностями Patient и Doctor.
 *
 * Пример: "Визит 2024-01-15 10:00, пациент Иванов И.И., врач Петров П.П."
 */
@Schema(description = "Сущность визита пациента к врачу")
@Data
@NoArgsConstructor
@ToString(exclude = {"patient", "doctor"})
@EqualsAndHashCode(exclude = {"patient", "doctor"})
@Entity
@Table(name = "Visit")
public class Visit {

    @Schema(description = "Уникальный идентификатор визита", example = "123e4567-e89b-12d3-a456-426614174000")
    @Id
    @Column(name = "visit_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Schema(description = "Дата и время визита", example = "2024-01-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "date_of_visit", nullable = false)
    private LocalDateTime dateOfVisit;

    @Schema(description = "Статус визита", example = "scheduled", allowableValues = {"scheduled", "completed", "cancelled"})
    @Column(name = "status")
    private String status;

    @Schema(description = "Симптомы пациента", example = "Головная боль, повышенное давление")
    @Column(name = "symptoms")
    private String symptoms;

    @Schema(description = "Диагноз", example = "Артериальная гипертензия")
    @Column(name = "diagnosis")
    private String diagnosis;

    @Schema(description = "Назначения врача", example = "Принимать препарат X по 1 таблетке 2 раза в день")
    @Column(name = "prescription")
    private String prescription;

    @Schema(description = "Пациент", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Schema(description = "Врач", requiredMode = Schema.RequiredMode.REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Builder
    public Visit(LocalDateTime dateOfVisit, String status, String symptoms,
                 String diagnosis, String prescription, Patient patient, Doctor doctor) {
        this.dateOfVisit = dateOfVisit;
        this.status = status;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.patient = patient;
        this.doctor = doctor;
    }
}