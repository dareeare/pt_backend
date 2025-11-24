package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для ServiceRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ServiceRepositoryParameterizedTests {

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor testDoctor1;
    private Doctor testDoctor2;

    @BeforeEach
    void setUp() {
        testDoctor1 = Doctor.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .specialty("Кардиолог")
                .phone("80291234567")
                .email("ivanov@clinic.com")
                .build();
        testDoctor1 = doctorRepository.save(testDoctor1);

        testDoctor2 = Doctor.builder()
                .firstName("Мария")
                .lastName("Петрова")
                .specialty("Терапевт")
                .phone("80292345678")
                .email("petrova@clinic.com")
                .build();
        testDoctor2 = doctorRepository.save(testDoctor2);
    }

    /**
     * Источник данных для тестирования создания услуг
     */
    private static Stream<Arguments> provideServiceDataForCreate() {
        return Stream.of(
                Arguments.of("Консультация кардиолога", new BigDecimal("100.00"), 60, "Первичный прием"),
                Arguments.of("УЗИ сердца", new BigDecimal("150.00"), 30, "Ультразвуковое исследование"),
                Arguments.of("ЭКГ", new BigDecimal("50.00"), 15, "Электрокардиография"),
                Arguments.of("Холтер мониторинг", new BigDecimal("200.00"), 1440, "Суточный мониторинг ЭКГ"),
                Arguments.of("Консультация терапевта", new BigDecimal("80.00"), 45, "Общий прием")
        );
    }

    /**
     * Параметризованный тест для создания услуги (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create service: {0}")
    @MethodSource("provideServiceDataForCreate")
    @DisplayName("save() should persist service with different data sets")
    void testSaveService_Parameterized(String nameOfService, BigDecimal cost, Integer durationMinutes, String information) {
        // Arrange
        Service service = Service.builder()
                .nameOfService(nameOfService)
                .cost(cost)
                .durationMinutes(durationMinutes)
                .information(information)
                .doctor(testDoctor1)
                .build();

        // Act
        Service saved = repository.save(service);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNameOfService()).isEqualTo(nameOfService);
        assertThat(saved.getCost()).isEqualTo(cost);
        assertThat(saved.getDurationMinutes()).isEqualTo(durationMinutes);
        assertThat(saved.getInformation()).isEqualTo(information);
        assertThat(saved.getDoctor().getId()).isEqualTo(testDoctor1.getId());
    }

    /**
     * Параметризованный тест для чтения услуги (READ)
     */
    @ParameterizedTest(name = "[{index}] Read service by ID: {0}")
    @MethodSource("provideServiceDataForCreate")
    @DisplayName("findById() should retrieve service with different data sets")
    void testFindById_Parameterized(String nameOfService, BigDecimal cost, Integer durationMinutes, String information) {
        // Arrange
        Service service = Service.builder()
                .nameOfService(nameOfService)
                .cost(cost)
                .durationMinutes(durationMinutes)
                .information(information)
                .doctor(testDoctor1)
                .build();
        Service saved = repository.save(service);

        // Act
        Optional<Service> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getNameOfService()).isEqualTo(nameOfService);
        assertThat(found.get().getCost()).isEqualTo(cost);
        assertThat(found.get().getDurationMinutes()).isEqualTo(durationMinutes);
    }

    /**
     * Источник данных для тестирования обновления услуг
     */
    private static Stream<Arguments> provideServiceDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        "Old Service 1", new BigDecimal("100.00"), 30, "Old info 1",
                        "New Service 1", new BigDecimal("120.00"), 45, "New info 1"
                ),
                Arguments.of(
                        "Old Service 2", new BigDecimal("50.00"), 15, "Old info 2",
                        "New Service 2", new BigDecimal("60.00"), 20, "New info 2"
                ),
                Arguments.of(
                        "Old Service 3", new BigDecimal("200.00"), 60, "Old info 3",
                        "New Service 3", new BigDecimal("250.00"), 90, "New info 3"
                )
        );
    }

    /**
     * Параметризованный тест для обновления услуги (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update service: {0} -> {4}")
    @MethodSource("provideServiceDataForUpdate")
    @DisplayName("updateById() should update service fields with different data sets")
    void testUpdateById_Parameterized(
            String oldName, BigDecimal oldCost, Integer oldDuration, String oldInfo,
            String newName, BigDecimal newCost, Integer newDuration, String newInfo) {
        // Arrange
        Service service = Service.builder()
                .nameOfService(oldName)
                .cost(oldCost)
                .durationMinutes(oldDuration)
                .information(oldInfo)
                .doctor(testDoctor1)
                .build();
        Service saved = repository.save(service);
        UUID serviceId = saved.getId();

        // Act
        int updated = repository.updateById(
                serviceId,
                newName,
                newCost,
                newDuration,
                newInfo,
                testDoctor2.getId()
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<Service> updatedService = repository.findById(serviceId);
        assertThat(updatedService).isPresent();
        assertThat(updatedService.get().getNameOfService()).isEqualTo(newName);
        assertThat(updatedService.get().getCost()).isEqualTo(newCost);
        assertThat(updatedService.get().getDurationMinutes()).isEqualTo(newDuration);
        assertThat(updatedService.get().getInformation()).isEqualTo(newInfo);
    }

    /**
     * Параметризованный тест для удаления услуги (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete service: {0}")
    @MethodSource("provideServiceDataForCreate")
    @DisplayName("deleteById() should remove service with different data sets")
    void testDeleteById_Parameterized(String nameOfService, BigDecimal cost, Integer durationMinutes, String information) {
        // Arrange
        Service service = Service.builder()
                .nameOfService(nameOfService)
                .cost(cost)
                .durationMinutes(durationMinutes)
                .information(information)
                .doctor(testDoctor1)
                .build();
        Service saved = repository.save(service);
        UUID serviceId = saved.getId();

        assertThat(repository.findById(serviceId)).isPresent();

        // Act
        repository.deleteById(serviceId);

        // Assert
        assertThat(repository.findById(serviceId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по имени
     */
    @ParameterizedTest
    @CsvSource({
            "Консультация, Консультация кардиолога, 100.00, 60",
            "УЗИ, УЗИ сердца, 150.00, 30",
            "ЭКГ, ЭКГ с расшифровкой, 50.00, 15"
    })
    @DisplayName("findByNameOfServiceContainingIgnoreCase() should find services by name")
    void testFindByNameContaining_Parameterized(String searchTerm, String fullName, String cost, Integer duration) {
        // Arrange
        Service service = Service.builder()
                .nameOfService(fullName)
                .cost(new BigDecimal(cost))
                .durationMinutes(duration)
                .doctor(testDoctor1)
                .build();
        repository.save(service);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Service> found = repository.findByNameOfServiceContainingIgnoreCase(searchTerm, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(s -> s.getNameOfService().contains(searchTerm));
    }

    /**
     * Параметризованный тест для поиска по врачу
     */
    @ParameterizedTest
    @CsvSource({
            "Консультация кардиолога, 100.00, 60",
            "УЗИ сердца, 150.00, 30",
            "ЭКГ, 50.00, 15"
    })
    @DisplayName("findByDoctorId() should find services by doctor")
    void testFindByDoctorId_Parameterized(String nameOfService, String cost, Integer duration) {
        // Arrange
        Service service = Service.builder()
                .nameOfService(nameOfService)
                .cost(new BigDecimal(cost))
                .durationMinutes(duration)
                .doctor(testDoctor1)
                .build();
        repository.save(service);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Service> found = repository.findByDoctorId(testDoctor1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(s -> s.getNameOfService().equals(nameOfService));
    }

    /**
     * Параметризованный тест для поиска по диапазону цен
     */
    @ParameterizedTest
    @CsvSource({
            "50.00, 100.00, Консультация терапевта, 80.00",
            "100.00, 200.00, Консультация кардиолога, 120.00",
            "150.00, 300.00, УЗИ сердца, 180.00"
    })
    @DisplayName("findByCostBetween() should find services by price range")
    void testFindByCostBetween_Parameterized(String minCost, String maxCost, String serviceName, String actualCost) {
        // Arrange
        Service service = Service.builder()
                .nameOfService(serviceName)
                .cost(new BigDecimal(actualCost))
                .durationMinutes(60)
                .doctor(testDoctor1)
                .build();
        repository.save(service);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Service> found = repository.findByCostBetween(
                Double.parseDouble(minCost), 
                Double.parseDouble(maxCost), 
                pageable
        );

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(s -> 
                s.getNameOfService().equals(serviceName) &&
                s.getCost().compareTo(new BigDecimal(minCost)) >= 0 &&
                s.getCost().compareTo(new BigDecimal(maxCost)) <= 0
        );
    }

    /**
     * Параметризованный тест для создания множества услуг
     */
    @ParameterizedTest
    @CsvSource({
            "3, Консультация",
            "5, УЗИ",
            "7, Анализ"
    })
    @DisplayName("save() should handle multiple services creation")
    void testSaveMultipleServices_Parameterized(int count, String namePrefix) {
        // Arrange & Act
        for (int i = 0; i < count; i++) {
            Service service = Service.builder()
                    .nameOfService(namePrefix + " " + i)
                    .cost(new BigDecimal("100.00"))
                    .durationMinutes(30)
                    .doctor(testDoctor1)
                    .build();
            repository.save(service);
        }

        // Assert
        Pageable pageable = PageRequest.of(0, 20);
        List<Service> found = repository.findByNameOfServiceContainingIgnoreCase(namePrefix, pageable);
        assertThat(found).hasSizeGreaterThanOrEqualTo(count);
    }
}

