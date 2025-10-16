package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ServiceRenderedRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceRenderedRepository serviceRenderedRepository;

    private Doctor doctor;
    private Patient patient;
    private Visit visit1;
    private Visit visit2;
    private Service service1;
    private Service service2;
    private ServiceRendered serviceRendered1;
    private ServiceRendered serviceRendered2;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+375251234567")
                .email("john.doe@example.com")
                .specialty("Cardiology")
                .build();

        patient = Patient.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phone("+375449876543")
                .email("jane.smith@example.com")
                .build();

        entityManager.persist(doctor);
        entityManager.persist(patient);
        entityManager.flush();

        visit1 = Visit.builder()
                .doctor(doctor)
                .patient(patient)
                .dateOfVisit(LocalDateTime.now())
                .diagnosis("Common cold")
                .symptoms("Cough, fever")
                .status("completed")
                .build();

        visit2 = Visit.builder()
                .doctor(doctor)
                .patient(patient)
                .dateOfVisit(LocalDateTime.now().minusDays(1))
                .diagnosis("Headache")
                .symptoms("Head pain")
                .status("completed")
                .build();

        service1 = Service.builder()
                .nameOfService("Consultation")
                .cost(BigDecimal.valueOf(1500.00))
                .durationMinutes(30)
                .information("Medical consultation")
                .doctor(doctor)
                .build();

        service2 = Service.builder()
                .nameOfService("Examination")
                .cost(BigDecimal.valueOf(2000.00))
                .durationMinutes(45)
                .information("Physical examination")
                .doctor(doctor)
                .build();

        entityManager.persist(visit1);
        entityManager.persist(visit2);
        entityManager.persist(service1);
        entityManager.persist(service2);
        entityManager.flush();

        serviceRendered1 = ServiceRendered.builder()
                .visit(visit1)
                .service(service1)
                .actualCost(BigDecimal.valueOf(1500.00))
                .build();

        serviceRendered2 = ServiceRendered.builder()
                .visit(visit2)
                .service(service2)
                .actualCost(BigDecimal.valueOf(1200.00))
                .build();

        entityManager.persist(serviceRendered1);
        entityManager.persist(serviceRendered2);
        entityManager.flush();
    }

    @Test
    void findByVisitId_shouldReturnServicesForVisit() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ServiceRendered> result = serviceRenderedRepository.findByVisitId(visit1.getId(), pageable);

        assertThat(result).hasSize(1);
        assertThat(result).extracting(ServiceRendered::getVisit)
                .allMatch(v -> v.getId().equals(visit1.getId()));
    }

    @Test
    void findByServiceId_shouldReturnRenderedServices() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ServiceRendered> result = serviceRenderedRepository.findByServiceId(service1.getId(), pageable);

        assertThat(result).hasSize(1);
        assertThat(result).extracting(ServiceRendered::getService)
                .allMatch(s -> s.getId().equals(service1.getId()));
    }

    @Test
    void findByVisitIdAndServiceId_shouldReturnSpecificServiceRendered() {
        Optional<ServiceRendered> result = serviceRenderedRepository.findByVisitIdAndServiceId(visit1.getId(), service1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getVisit().getId()).isEqualTo(visit1.getId());
        assertThat(result.get().getService().getId()).isEqualTo(service1.getId());
    }

    @Test
    void calculateTotalCostByVisitId_shouldReturnSumOfCosts() {
        BigDecimal result = serviceRenderedRepository.calculateTotalCostByVisitId(visit1.getId());

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
    }

    @Test
    void updateById_shouldUpdateServiceRendered() {
        UUID newVisitId = visit2.getId();
        UUID newServiceId = service2.getId();
        BigDecimal newActualCost = BigDecimal.valueOf(2000.00);

        int updatedCount = serviceRenderedRepository.updateById(
                serviceRendered1.getId(),
                newVisitId,
                newServiceId,
                newActualCost
        );

        assertThat(updatedCount).isEqualTo(1);

        entityManager.clear();
        ServiceRendered updatedServiceRendered = entityManager.find(ServiceRendered.class, serviceRendered1.getId());
        
        assertThat(updatedServiceRendered.getActualCost()).isEqualByComparingTo(newActualCost);
        assertThat(updatedServiceRendered.getVisit().getId()).isEqualTo(newVisitId);
        assertThat(updatedServiceRendered.getService().getId()).isEqualTo(newServiceId);
    }
}