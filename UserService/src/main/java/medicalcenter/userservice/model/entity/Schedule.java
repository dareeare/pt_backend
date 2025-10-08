package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность представляет рабочее расписание врача на конкретный день.
 * Содержит временные интервалы (начало и конец работы) для каждого рабочего дня.
 * Используется для управления доступностью врачей и планирования приемов.
 *
 * Пример: "Пн 09:00-18:00", "Вт 10:00-19:00"
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "work_day", nullable = false)
    private LocalDate workDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Builder
    public Schedule(LocalDateTime startTime, LocalDateTime endTime,
                    LocalDate workDay, Doctor doctor) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.workDay = workDay;
        this.doctor = doctor;
    }
}