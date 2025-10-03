package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность представляет отзыв пациента о враче после завершенного визита.
 * Включает рейтинг (1-5 звезд), текстовый комментарий и флаги модерации.
 * Уникальное ограничение гарантирует один отзыв на визит.
 * Автоматически обновляет рейтинг врача через триггеры.
 *
 * Пример: "Отзыв пациента Иванова на визит №456: 5 звезд, 'Отличный врач!'"
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "doctorreviews")
public class DoctorReview {
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "is_edited")
    private Boolean isEdited = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public DoctorReview(Patient patient, Doctor doctor, Visit visit,
                         Integer rating, String comment, Boolean isApproved,
                         Boolean isEdited) {
        this.patient = patient;
        this.doctor = doctor;
        this.visit = visit;
        this.rating = rating;
        this.comment = comment;
        this.isApproved = isApproved;
        this.isEdited = isEdited;
    }
}
