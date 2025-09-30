package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString(exclude = "doctor")
@EqualsAndHashCode(exclude = "doctor")
@Entity
@Table(name = "Service")
public class Service {
    @Id
    @Column(name = "service_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name_of_service", nullable = false)
    private String nameOfService;

    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "information")
    private String information;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Builder
    public Service(String nameOfService, BigDecimal cost, Integer durationMinutes,
                   String information, Doctor doctor) {
        this.nameOfService = nameOfService;
        this.cost = cost;
        this.durationMinutes = durationMinutes;
        this.information = information;
        this.doctor = doctor;
    }
}