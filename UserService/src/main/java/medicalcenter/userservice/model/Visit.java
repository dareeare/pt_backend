package medicalcenter.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private LocalDateTime dateOfVisit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "status")
    @Pattern(regexp = "scheduled|completed|cancelled")
    private String status;

    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "diagnosis")
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
