package medicalcenter.userservice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сущность представляет медицинскую услугу, предоставляемую врачом.
 * Каждая услуга имеет фиксированную стоимость, продолжительность и описание.
 * Связь ManyToOne с Doctor указывает, что врач может предоставлять несколько услуг.
 *
 * Пример: "Консультация кардиолога", "УЗИ сердца", "ЭКГ с расшифровкой"
 */
@Schema(description = "Сущность медицинской услуги")
@Data
@NoArgsConstructor
@ToString(exclude = "doctor")
@EqualsAndHashCode(exclude = "doctor")
@Entity
@Table(name = "Service")
public class Service {

    @Schema(description = "Уникальный идентификатор услуги", example = "123e4567-e89b-12d3-a456-426614174000")
    @Id
    @Column(name = "service_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Schema(description = "Название услуги", example = "Консультация кардиолога", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "name_of_service", nullable = false)
    private String nameOfService;

    @Schema(description = "Стоимость услуги", example = "100.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @Schema(description = "Продолжительность услуги в минутах", example = "60", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Schema(description = "Дополнительная информация об услуге", example = "Первичный прием с осмотром")
    @Column(name = "information")
    private String information;

    @Schema(description = "Врач, предоставляющий услугу", requiredMode = Schema.RequiredMode.REQUIRED)
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