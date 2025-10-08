package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность для обработки исключительных ситуаций в расписании врача.
 * Может отмечать как нерабочие дни (отпуск, больничный), так и дополнительные рабочие дни.
 * Используется для временного изменения стандартного расписания.
 *
 * Пример: "15.01.2024 - больничный", "20.01.2024 - дополнительный прием"
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "scheduleexceptions")
public class ScheduleException {
    @Id
    @Column(name = "exception_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "exception_date", nullable = false)
    private LocalDate exceptionDate;

    @Column(name = "reason")
    private String reason;

    @Column(name = "is_working_day")
    private Boolean isWorkingDay = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public ScheduleException(Doctor doctor, LocalDate exceptionDate,
                              String reason, Boolean isWorkingDay) {
        this.doctor = doctor;
        this.exceptionDate = exceptionDate;
        this.reason = reason;
        this.isWorkingDay = isWorkingDay;
    }
}
