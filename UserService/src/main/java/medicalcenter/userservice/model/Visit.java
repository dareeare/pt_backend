package medicalcenter.userservice.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
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

    @Column(name = "date_of_visit")
    private LocalDateTime dateOfVisit;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id")
    private Patient patient;

    @Column(name = "status")
    private String status;

    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "prescription")
    private String prescription;

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
