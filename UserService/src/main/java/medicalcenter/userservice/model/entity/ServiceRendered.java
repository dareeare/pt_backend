package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Связующая сущность между Visit и Service, фиксирует факт оказания услуги.
 * Может содержать фактическую стоимость, которая может отличаться от базовой.
 * Позволяет отслеживать, какие услуги были предоставлены во время визита.
 *
 * Пример: "На визите №123 оказана услуга 'Консультация' за 1500 руб"
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "servicerendered")
public class ServiceRendered {
    @Id
    @Column(name = "sr_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "actual_cost", nullable = false)
    private BigDecimal actualCost;

    @Builder
    public ServiceRendered(Visit visit, Service service, BigDecimal actualCost) {
        this.visit = visit;
        this.service = service;
        this.actualCost = actualCost;
    }
}
