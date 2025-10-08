package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Сущность представляет конкретные временные интервалы для записи пациентов.
 * Каждый слот связан с врачом, датой и временем начала/окончания.
 * Слот может быть свободным (visit_id = NULL) или занятым конкретным визитом.
 * Обеспечивает контроль за пересечением временных интервалов.
 *
 * Пример: "Слот 15.01.2024 10:00-10:30 для врача Петрова"
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "TimeSlots")
public class TimeSlot {
    @Id
    @Column(name = "slot_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @Builder
    public TimeSlot(Doctor doctor, LocalDate slotDate, LocalTime startTime,
                     LocalTime endTime, Visit visit) {
        this.doctor = doctor;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.visit = visit;
    }
}
