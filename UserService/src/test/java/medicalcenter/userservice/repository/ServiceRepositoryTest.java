package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ServiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceRepository serviceRepository;

    private Doctor doctor;
    private Service service1;
    private Service service2;
    private Service service3;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .email("john.doe@example.com")
                .specialty("Cardiology")
                .build();
        entityManager.persistAndFlush(doctor);

        service1 = Service.builder()
                .nameOfService("Консультация кардиолога")
                .cost(BigDecimal.valueOf(1500.00))
                .durationMinutes(30)
                .information("Первичная консультация")
                .doctor(doctor)
                .build();

        service2 = Service.builder()
                .nameOfService("УЗИ сердца")
                .cost(BigDecimal.valueOf(2500.00))
                .durationMinutes(45)
                .information("Ультразвуковое исследование")
                .doctor(doctor)
                .build();

        service3 = Service.builder()
                .nameOfService("ЭКГ с расшифровкой")
                .cost(BigDecimal.valueOf(800.00))
                .durationMinutes(20)
                .information("Электрокардиограмма")
                .doctor(doctor)
                .build();

        entityManager.persist(service1);
        entityManager.persist(service2);
        entityManager.persist(service3);
        entityManager.flush();
    }

    @Test
    void findByNameOfServiceContainingIgnoreCase_shouldReturnMatchingServices() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchName = "кардиолог";

        List<Service> result = serviceRepository.findByNameOfServiceContainingIgnoreCase(searchName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNameOfService()).isEqualTo("Консультация кардиолога");
    }

    @Test
    void findByNameOfServiceContainingIgnoreCase_shouldBeCaseInsensitive() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchName = "УЗИ";

        List<Service> result = serviceRepository.findByNameOfServiceContainingIgnoreCase(searchName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNameOfService()).isEqualTo("УЗИ сердца");
    }

    @Test
    void findByDoctorId_shouldReturnServicesForDoctor() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Service> result = serviceRepository.findByDoctorId(doctor.getId(), pageable);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Service::getDoctor)
                .allMatch(d -> d.getId().equals(doctor.getId()));
    }

    @Test
    void findByDoctorId_shouldReturnEmptyListForNonExistingDoctor() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID nonExistingDoctorId = UUID.randomUUID();

        List<Service> result = serviceRepository.findByDoctorId(nonExistingDoctorId, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByCostBetween_shouldReturnServicesInCostRange() {
        Pageable pageable = PageRequest.of(0, 10);
        Double minCost = 1000.00;
        Double maxCost = 2000.00;

        List<Service> result = serviceRepository.findByCostBetween(minCost, maxCost, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNameOfService()).isEqualTo("Консультация кардиолога");
        assertThat(result.get(0).getCost()).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
    }

    @Test
    void findByCostBetween_shouldReturnMultipleServices() {
        Pageable pageable = PageRequest.of(0, 10);
        Double minCost = 500.00;
        Double maxCost = 3000.00;

        List<Service> result = serviceRepository.findByCostBetween(minCost, maxCost, pageable);

        assertThat(result).hasSize(3);
    }

    @Test
    void updateById_shouldUpdateService() {
        String newName = "Расширенная консультация";
        BigDecimal newCost = BigDecimal.valueOf(2000.00);
        Integer newDuration = 60;
        String newInformation = "Расширенная консультация с анализами";

        int updatedCount = serviceRepository.updateById(
                service1.getId(),
                newName,
                newCost,
                newDuration,
                newInformation,
                doctor.getId()
        );

        assertThat(updatedCount).isEqualTo(1);

        entityManager.clear();
        Service updatedService = entityManager.find(Service.class, service1.getId());
        
        assertThat(updatedService.getNameOfService()).isEqualTo(newName);
        assertThat(updatedService.getCost()).isEqualByComparingTo(newCost);
        assertThat(updatedService.getDurationMinutes()).isEqualTo(newDuration);
        assertThat(updatedService.getInformation()).isEqualTo(newInformation);
    }

    @Test
    void updateById_shouldReturnZeroForNonExistingId() {
        UUID nonExistingId = UUID.randomUUID();

        int updatedCount = serviceRepository.updateById(
                nonExistingId,
                "Test",
                BigDecimal.valueOf(1000.00),
                30,
                "Test info",
                doctor.getId()
        );

        assertThat(updatedCount).isEqualTo(0);
    }

    @Test
    void findAll_shouldReturnAllServices() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Service> result = serviceRepository.findAll(pageable).getContent();

        assertThat(result).hasSize(3);
    }

    @Test
    void findById_shouldReturnService() {
        Service result = serviceRepository.findById(service1.getId()).orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getNameOfService()).isEqualTo("Консультация кардиолога");
    }

    @Test
    void save_shouldPersistNewService() {
        Service newService = Service.builder()
                .nameOfService("Новая услуга")
                .cost(BigDecimal.valueOf(1200.00))
                .durationMinutes(25)
                .information("Описание новой услуги")
                .doctor(doctor)
                .build();

        Service saved = serviceRepository.save(newService);

        assertThat(saved.getId()).isNotNull();
        assertThat(entityManager.find(Service.class, saved.getId())).isNotNull();
    }

    @Test
    void delete_shouldRemoveService() {
        serviceRepository.delete(service1);

        assertThat(entityManager.find(Service.class, service1.getId())).isNull();
    }

    @Test
    void findByNameOfServiceContainingIgnoreCase_shouldReturnEmptyForNonMatchingName() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchName = "несуществующаяуслуга";

        List<Service> result = serviceRepository.findByNameOfServiceContainingIgnoreCase(searchName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByCostBetween_shouldReturnEmptyForNoMatchingCost() {
        Pageable pageable = PageRequest.of(0, 10);
        Double minCost = 5000.00;
        Double maxCost = 10000.00;

        List<Service> result = serviceRepository.findByCostBetween(minCost, maxCost, pageable);

        assertThat(result).isEmpty();
    }
}