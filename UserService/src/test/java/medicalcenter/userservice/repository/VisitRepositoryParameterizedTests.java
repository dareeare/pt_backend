package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.Visit;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для VisitRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class VisitRepositoryParameterizedTests {

    @Autowired
    private VisitRepository repository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Doctor testDoctor1;
    private Doctor testDoctor2;
    private Patient testPatient1;
    private Patient testPatient2;

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

        testPatient1 = Patient.builder()
                .firstName("Петр")
                .lastName("Сидоров")
                .phone("80293456789")
                .email("sidorov@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
        testPatient1 = patientRepository.save(testPatient1);

        testPatient2 = Patient.builder()
                .firstName("Анна")
                .lastName("Козлова")
                .phone("80294567890")
                .email("kozlova@example.com")
                .gender("F")
                .dateOfBirth(LocalDate.of(1990, 5, 5))
                .build();
        testPatient2 = patientRepository.save(testPatient2);
    }

    /**
     * Источник данных для тестирования создания визитов
     */
    private static Stream<Arguments> provideVisitDataForCreate() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2024, 1, 15, 10, 0), "scheduled", "Головная боль", null, null),
                Arguments.of(LocalDateTime.of(2024, 2, 20, 14, 30), "completed", "Боль в груди", "Гипертония", "Принимать препарат X"),
                Arguments.of(LocalDateTime.of(2024, 3, 25, 9, 0), "scheduled", "Кашель", null, null),
                Arguments.of(LocalDateTime.of(2024, 4, 10, 16, 0), "completed", "Усталость", "ОРВИ", "Отдых и обильное питье"),
                Arguments.of(LocalDateTime.of(2024, 5, 5, 11, 30), "cancelled", "Профилактический осмотр", null, null)
        );
    }

    /**
     * Параметризованный тест для создания визита (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create visit: {0}, status: {1}")
    @MethodSource("provideVisitDataForCreate")
    @DisplayName("save() should persist visit with different data sets")
    void testSaveVisit_Parameterized(LocalDateTime dateOfVisit, String status, String symptoms, 
                                     String diagnosis, String prescription) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(dateOfVisit)
                .status(status)
                .symptoms(symptoms)
                .diagnosis(diagnosis)
                .prescription(prescription)
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();

        // Act
        Visit saved = repository.save(visit);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDateOfVisit()).isEqualTo(dateOfVisit);
        assertThat(saved.getStatus()).isEqualTo(status);
        assertThat(saved.getSymptoms()).isEqualTo(symptoms);
        assertThat(saved.getDiagnosis()).isEqualTo(diagnosis);
        assertThat(saved.getPrescription()).isEqualTo(prescription);
        assertThat(saved.getPatient().getId()).isEqualTo(testPatient1.getId());
        assertThat(saved.getDoctor().getId()).isEqualTo(testDoctor1.getId());
    }

    /**
     * Параметризованный тест для чтения визита (READ)
     */
    @ParameterizedTest(name = "[{index}] Read visit by ID: {1}")
    @MethodSource("provideVisitDataForCreate")
    @DisplayName("findById() should retrieve visit with different data sets")
    void testFindById_Parameterized(LocalDateTime dateOfVisit, String status, String symptoms, 
                                    String diagnosis, String prescription) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(dateOfVisit)
                .status(status)
                .symptoms(symptoms)
                .diagnosis(diagnosis)
                .prescription(prescription)
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        Visit saved = repository.save(visit);

        // Act
        Optional<Visit> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getDateOfVisit()).isEqualTo(dateOfVisit);
        assertThat(found.get().getStatus()).isEqualTo(status);
        assertThat(found.get().getSymptoms()).isEqualTo(symptoms);
    }

    /**
     * Источник данных для тестирования обновления визитов
     */
    private static Stream<Arguments> provideVisitDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2024, 1, 1, 10, 0), "scheduled", "Old symptoms 1", null, null,
                        LocalDateTime.of(2024, 1, 2, 11, 0), "completed", "New symptoms 1", "Diagnosis 1", "Prescription 1"
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 2, 1, 14, 0), "scheduled", "Old symptoms 2", null, null,
                        LocalDateTime.of(2024, 2, 2, 15, 0), "cancelled", "New symptoms 2", null, null
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 3, 1, 9, 0), "completed", "Old symptoms 3", "Old diagnosis", "Old prescription",
                        LocalDateTime.of(2024, 3, 2, 10, 0), "completed", "New symptoms 3", "New diagnosis", "New prescription"
                )
        );
    }

    /**
     * Параметризованный тест для обновления визита (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update visit: {1} -> {6}")
    @MethodSource("provideVisitDataForUpdate")
    @DisplayName("updateById() should update visit fields with different data sets")
    void testUpdateById_Parameterized(
            LocalDateTime oldDate, String oldStatus, String oldSymptoms, String oldDiagnosis, String oldPrescription,
            LocalDateTime newDate, String newStatus, String newSymptoms, String newDiagnosis, String newPrescription) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(oldDate)
                .status(oldStatus)
                .symptoms(oldSymptoms)
                .diagnosis(oldDiagnosis)
                .prescription(oldPrescription)
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        Visit saved = repository.save(visit);
        UUID visitId = saved.getId();

        // Act
        int updated = repository.updateById(
                visitId,
                newDate,
                testDoctor2.getId(),
                testPatient2.getId(),
                newStatus,
                newSymptoms,
                newDiagnosis,
                newPrescription
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<Visit> updatedVisit = repository.findById(visitId);
        assertThat(updatedVisit).isPresent();
        assertThat(updatedVisit.get().getDateOfVisit()).isEqualTo(newDate);
        assertThat(updatedVisit.get().getStatus()).isEqualTo(newStatus);
        assertThat(updatedVisit.get().getSymptoms()).isEqualTo(newSymptoms);
        assertThat(updatedVisit.get().getDiagnosis()).isEqualTo(newDiagnosis);
        assertThat(updatedVisit.get().getPrescription()).isEqualTo(newPrescription);
    }

    /**
     * Параметризованный тест для удаления визита (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete visit: {1}")
    @MethodSource("provideVisitDataForCreate")
    @DisplayName("deleteById() should remove visit with different data sets")
    void testDeleteById_Parameterized(LocalDateTime dateOfVisit, String status, String symptoms, 
                                      String diagnosis, String prescription) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(dateOfVisit)
                .status(status)
                .symptoms(symptoms)
                .diagnosis(diagnosis)
                .prescription(prescription)
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        Visit saved = repository.save(visit);
        UUID visitId = saved.getId();

        assertThat(repository.findById(visitId)).isPresent();

        // Act
        repository.deleteById(visitId);

        // Assert
        assertThat(repository.findById(visitId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по статусу
     */
    @ParameterizedTest
    @CsvSource({
            "scheduled, 2024-01-15T10:00, Головная боль",
            "completed, 2024-02-20T14:30, Боль в груди",
            "cancelled, 2024-03-25T09:00, Кашель"
    })
    @DisplayName("findByStatus() should find visits by status")
    void testFindByStatus_Parameterized(String status, LocalDateTime dateOfVisit, String symptoms) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(dateOfVisit)
                .status(status)
                .symptoms(symptoms)
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        repository.save(visit);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> found = repository.findByStatus(status, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(v -> v.getStatus().equals(status));
    }

    /**
     * Параметризованный тест для поиска по ID доктора
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15T10:00, scheduled, Головная боль",
            "2024-02-20T14:30, completed, Боль в груди",
            "2024-03-25T09:00, scheduled, Кашель"
    })
    @DisplayName("findByDoctorId() should find visits by doctor")
    void testFindByDoctorId_Parameterized(LocalDateTime dateOfVisit, String status, String symptoms) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(dateOfVisit)
                .status(status)
                .symptoms(symptoms)
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        repository.save(visit);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> found = repository.findByDoctorId(testDoctor1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(v -> v.getSymptoms().equals(symptoms));
    }

    /**
     * Параметризованный тест для поиска по ID пациента
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15T10:00, scheduled, Головная боль",
            "2024-02-20T14:30, completed, Боль в груди",
            "2024-03-25T09:00, scheduled, Кашель"
    })
    @DisplayName("findByPatientId() should find visits by patient")
    void testFindByPatientId_Parameterized(LocalDateTime dateOfVisit, String status, String symptoms) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(dateOfVisit)
                .status(status)
                .symptoms(symptoms)
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        repository.save(visit);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> found = repository.findByPatientId(testPatient1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(v -> v.getSymptoms().equals(symptoms));
    }

    /**
     * Параметризованный тест для поиска по фамилии пациента
     */
    @ParameterizedTest
    @CsvSource({
            "Сидоров, 2024-01-15T10:00, Головная боль",
            "Козлова, 2024-02-20T14:30, Боль в груди"
    })
    @DisplayName("findByPatientLastNameContainingIgnoreCase() should find visits by patient last name")
    void testFindByPatientLastName_Parameterized(String lastName, LocalDateTime dateOfVisit, String symptoms) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName("Test")
                .lastName(lastName)
                .phone("80290000000")
                .email("test@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        patient = patientRepository.save(patient);

        Visit visit = Visit.builder()
                .dateOfVisit(dateOfVisit)
                .status("scheduled")
                .symptoms(symptoms)
                .patient(patient)
                .doctor(testDoctor1)
                .build();
        repository.save(visit);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> found = repository.findByPatientLastNameContainingIgnoreCase(lastName, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(v -> v.getSymptoms().equals(symptoms));
    }

    /**
     * Параметризованный тест для поиска визитов в диапазоне дат
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-01T00:00, 2024-01-31T23:59, 2024-01-15T10:00",
            "2024-02-01T00:00, 2024-02-28T23:59, 2024-02-20T14:30",
            "2024-03-01T00:00, 2024-03-31T23:59, 2024-03-25T09:00"
    })
    @DisplayName("findByDateOfVisitBetween() should find visits in date range")
    void testFindByDateBetween_Parameterized(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime visitDate) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(visitDate)
                .status("scheduled")
                .symptoms("Test symptoms")
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        repository.save(visit);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> found = repository.findByDateOfVisitBetween(startDate, endDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(v -> v.getDateOfVisit().equals(visitDate));
    }

    /**
     * Параметризованный тест для поиска прошлых визитов пациента
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15T10:00, 2024-02-01T00:00",
            "2024-02-20T14:30, 2024-03-01T00:00",
            "2024-03-25T09:00, 2024-04-01T00:00"
    })
    @DisplayName("findPastVisitsByPatientId() should find past visits for patient")
    void testFindPastVisitsByPatientId_Parameterized(LocalDateTime visitDate, LocalDateTime currentDate) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(visitDate)
                .status("completed")
                .symptoms("Past symptoms")
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        repository.save(visit);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> found = repository.findPastVisitsByPatientId(testPatient1.getId(), currentDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(v -> v.getDateOfVisit().equals(visitDate));
    }

    /**
     * Параметризованный тест для поиска будущих визитов пациента
     */
    @ParameterizedTest
    @CsvSource({
            "2024-02-15T10:00, 2024-01-01T00:00",
            "2024-03-20T14:30, 2024-02-01T00:00",
            "2024-04-25T09:00, 2024-03-01T00:00"
    })
    @DisplayName("findFutureVisitsByPatientId() should find future visits for patient")
    void testFindFutureVisitsByPatientId_Parameterized(LocalDateTime visitDate, LocalDateTime currentDate) {
        // Arrange
        Visit visit = Visit.builder()
                .dateOfVisit(visitDate)
                .status("scheduled")
                .symptoms("Future symptoms")
                .patient(testPatient1)
                .doctor(testDoctor1)
                .build();
        repository.save(visit);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Visit> found = repository.findFutureVisitsByPatientId(testPatient1.getId(), currentDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(v -> v.getDateOfVisit().equals(visitDate));
    }
}

