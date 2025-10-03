package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Центральная сущность, представляющая визит пациента к врачу.
 * Содержит медицинскую информацию: симптомы, диагноз, назначения.
 * Имеет статусы: 'scheduled', 'completed', 'cancelled'.
 * Связана с оказанными услугами, отзывом и временным слотом.
 *
 * Пример: "Визит пациента Петрова к кардиологу 15.01.2024 10:00"
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "Visit")
public class Visit {
    @Id
    @Column(name = "visit_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID visitId;

    @Column(name = "date_of_visit", nullable = false)
    @NotNull(message = "Date of visit should have value")
    private LocalDateTime dateOfVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    @NotNull
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    @NotNull
    private Patient patient;

    @Column(name = "status")
    @Pattern(regexp = "scheduled|completed|cancelled")
    private String status;

    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "diagnosis")
    @NotBlank(message = "Diagnosis should not be empty")
    private String diagnosis;

    @Column(name = "prescription")
    private String prescription;


    @Builder
    public Visit(LocalDateTime dateOfVisit, Doctor doctor, Patient patient,
                 String status, String symptoms, String diagnosis, String prescription) {
        this.dateOfVisit = dateOfVisit;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
    }
}
