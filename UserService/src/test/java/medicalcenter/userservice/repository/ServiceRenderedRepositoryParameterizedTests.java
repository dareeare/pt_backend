package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для ServiceRenderedRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ServiceRenderedRepositoryParameterizedTests {

    @Autowired
    private ServiceRenderedRepository repository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Service testService1;
    private Service testService2;
    private Visit testVisit1;
    private Visit testVisit2;

    @BeforeEach
    void setUp() {
        Doctor doctor = Doctor.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .specialty("Кардиолог")
                .phone("80291234567")
                .email("ivanov@clinic.com")
                .build();
        doctor = doctorRepository.save(doctor);

        Patient patient = Patient.builder()
                .firstName("Петр")
                .lastName("Сидоров")
                .phone("80293456789")
                .email("sidorov@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
        patient = patientRepository.save(patient);

        testService1 = Service.builder()
                .nameOfService("Консультация кардиолога")
                .cost(new BigDecimal("100.00"))
                .durationMinutes(60)
                .information("Первичный прием")
                .doctor(doctor)
                .build();
        testService1 = serviceRepository.save(testService1);

        testService2 = Service.builder()
                .nameOfService("ЭКГ")
                .cost(new BigDecimal("50.00"))
                .durationMinutes(15)
                .information("Электрокардиография")
                .doctor(doctor)
                .build();
        testService2 = serviceRepository.save(testService2);

        testVisit1 = Visit.builder()
                .dateOfVisit(LocalDateTime.of(2024, 1, 15, 10, 0))
                .status("completed")
                .symptoms("Test symptoms 1")
                .patient(patient)
                .doctor(doctor)
                .build();
        testVisit1 = visitRepository.save(testVisit1);

        testVisit2 = Visit.builder()
                .dateOfVisit(LocalDateTime.of(2024, 2, 20, 14, 0))
                .status("completed")
                .symptoms("Test symptoms 2")
                .patient(patient)
                .doctor(doctor)
                .build();
        testVisit2 = visitRepository.save(testVisit2);
    }

    /**
     * Источник данных для тестирования создания оказанных услуг
     */
    private static Stream<Arguments> provideServiceRenderedDataForCreate() {
        return Stream.of(
                Arguments.of(new BigDecimal("100.00")),
                Arguments.of(new BigDecimal("150.00")),
                Arguments.of(new BigDecimal("75.50")),
                Arguments.of(new BigDecimal("200.00")),
                Arguments.of(new BigDecimal("50.00"))
        );
    }

    /**
     * Параметризованный тест для создания оказанной услуги (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create service rendered: cost={0}")
    @MethodSource("provideServiceRenderedDataForCreate")
    @DisplayName("save() should persist service rendered with different data sets")
    void testSaveServiceRendered_Parameterized(BigDecimal actualCost) {
        // Arrange
        ServiceRendered serviceRendered = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(actualCost)
                .build();

        // Act
        ServiceRendered saved = repository.save(serviceRendered);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getActualCost()).isEqualTo(actualCost);
        assertThat(saved.getVisit().getId()).isEqualTo(testVisit1.getId());
        assertThat(saved.getService().getId()).isEqualTo(testService1.getId());
    }

    /**
     * Параметризованный тест для чтения оказанной услуги (READ)
     */
    @ParameterizedTest(name = "[{index}] Read service rendered by ID: cost={0}")
    @MethodSource("provideServiceRenderedDataForCreate")
    @DisplayName("findById() should retrieve service rendered with different data sets")
    void testFindById_Parameterized(BigDecimal actualCost) {
        // Arrange
        ServiceRendered serviceRendered = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(actualCost)
                .build();
        ServiceRendered saved = repository.save(serviceRendered);

        // Act
        Optional<ServiceRendered> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getActualCost()).isEqualTo(actualCost);
    }

    /**
     * Источник данных для тестирования обновления оказанных услуг
     */
    private static Stream<Arguments> provideServiceRenderedDataForUpdate() {
        return Stream.of(
                Arguments.of(new BigDecimal("100.00"), new BigDecimal("120.00")),
                Arguments.of(new BigDecimal("50.00"), new BigDecimal("60.00")),
                Arguments.of(new BigDecimal("200.00"), new BigDecimal("180.00"))
        );
    }

    /**
     * Параметризованный тест для обновления оказанной услуги (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update service rendered: {0} -> {1}")
    @MethodSource("provideServiceRenderedDataForUpdate")
    @DisplayName("updateById() should update service rendered fields with different data sets")
    void testUpdateById_Parameterized(BigDecimal oldCost, BigDecimal newCost) {
        // Arrange
        ServiceRendered serviceRendered = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(oldCost)
                .build();
        ServiceRendered saved = repository.save(serviceRendered);
        UUID serviceRenderedId = saved.getId();

        // Act
        int updated = repository.updateById(
                serviceRenderedId,
                testVisit2.getId(),
                testService2.getId(),
                newCost
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<ServiceRendered> updatedSR = repository.findById(serviceRenderedId);
        assertThat(updatedSR).isPresent();
        assertThat(updatedSR.get().getActualCost()).isEqualByComparingTo(newCost);
    }

    /**
     * Параметризованный тест для удаления оказанной услуги (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete service rendered: cost={0}")
    @MethodSource("provideServiceRenderedDataForCreate")
    @DisplayName("deleteById() should remove service rendered with different data sets")
    void testDeleteById_Parameterized(BigDecimal actualCost) {
        // Arrange
        ServiceRendered serviceRendered = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(actualCost)
                .build();
        ServiceRendered saved = repository.save(serviceRendered);
        UUID serviceRenderedId = saved.getId();

        assertThat(repository.findById(serviceRenderedId)).isPresent();

        // Act
        repository.deleteById(serviceRenderedId);

        // Assert
        assertThat(repository.findById(serviceRenderedId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по ID визита
     */
    @ParameterizedTest
    @CsvSource({
            "100.00",
            "150.00",
            "75.50"
    })
    @DisplayName("findByVisitId() should find services rendered by visit")
    void testFindByVisitId_Parameterized(String actualCostStr) {
        // Arrange
        BigDecimal actualCost = new BigDecimal(actualCostStr);
        ServiceRendered serviceRendered = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(actualCost)
                .build();
        repository.save(serviceRendered);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<ServiceRendered> found = repository.findByVisitId(testVisit1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(sr -> sr.getActualCost().compareTo(actualCost) == 0);
    }

    /**
     * Параметризованный тест для поиска по ID услуги
     */
    @ParameterizedTest
    @CsvSource({
            "100.00",
            "150.00",
            "75.50"
    })
    @DisplayName("findByServiceId() should find services rendered by service")
    void testFindByServiceId_Parameterized(String actualCostStr) {
        // Arrange
        BigDecimal actualCost = new BigDecimal(actualCostStr);
        ServiceRendered serviceRendered = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(actualCost)
                .build();
        repository.save(serviceRendered);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<ServiceRendered> found = repository.findByServiceId(testService1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(sr -> sr.getActualCost().compareTo(actualCost) == 0);
    }

    /**
     * Параметризованный тест для поиска по ID визита и услуги
     */
    @ParameterizedTest
    @CsvSource({
            "100.00",
            "150.00",
            "75.50"
    })
    @DisplayName("findByVisitIdAndServiceId() should find service rendered by visit and service")
    void testFindByVisitIdAndServiceId_Parameterized(String actualCostStr) {
        // Arrange
        BigDecimal actualCost = new BigDecimal(actualCostStr);
        ServiceRendered serviceRendered = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(actualCost)
                .build();
        repository.save(serviceRendered);

        // Act
        Optional<ServiceRendered> found = repository.findByVisitIdAndServiceId(
                testVisit1.getId(), 
                testService1.getId()
        );

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getActualCost()).isEqualTo(actualCost);
    }

    /**
     * Параметризованный тест для расчета общей стоимости по визиту
     */
    @ParameterizedTest
    @CsvSource({
            "100.00, 50.00, 150.00",
            "200.00, 100.00, 300.00",
            "75.50, 24.50, 100.00"
    })
    @DisplayName("calculateTotalCostByVisitId() should calculate total cost for visit")
    void testCalculateTotalCost_Parameterized(String cost1Str, String cost2Str, String expectedTotalStr) {
        // Arrange
        BigDecimal cost1 = new BigDecimal(cost1Str);
        BigDecimal cost2 = new BigDecimal(cost2Str);
        BigDecimal expectedTotal = new BigDecimal(expectedTotalStr);

        ServiceRendered sr1 = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService1)
                .actualCost(cost1)
                .build();
        repository.save(sr1);

        ServiceRendered sr2 = ServiceRendered.builder()
                .visit(testVisit1)
                .service(testService2)
                .actualCost(cost2)
                .build();
        repository.save(sr2);

        // Act
        BigDecimal totalCost = repository.calculateTotalCostByVisitId(testVisit1.getId());

        // Assert
        assertThat(totalCost).isNotNull();
        assertThat(totalCost).isEqualTo(expectedTotal);
    }

    /**
     * Параметризованный тест для создания множественных услуг
     */
    @ParameterizedTest
    @CsvSource({
            "3, 100.00",
            "5, 50.00",
            "7, 75.00"
    })
    @DisplayName("save() should handle multiple service rendered creation")
    void testSaveMultipleServiceRendered_Parameterized(int count, String costStr) {
        // Arrange & Act
        BigDecimal cost = new BigDecimal(costStr);
        for (int i = 0; i < count; i++) {
            ServiceRendered sr = ServiceRendered.builder()
                    .visit(testVisit1)
                    .service(testService1)
                    .actualCost(cost.add(new BigDecimal(i)))
                    .build();
            repository.save(sr);
        }

        // Assert
        Pageable pageable = PageRequest.of(0, 20);
        List<ServiceRendered> found = repository.findByVisitId(testVisit1.getId(), pageable);
        assertThat(found).hasSizeGreaterThanOrEqualTo(count);
    }
}

