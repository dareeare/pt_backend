package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.ScheduleException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для ScheduleExceptionRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ScheduleExceptionRepositoryParameterizedTests {

    @Autowired
    private ScheduleExceptionRepository repository;

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
     * Источник данных для тестирования создания исключений в расписании
     */
    private static Stream<Arguments> provideScheduleExceptionDataForCreate() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 1, 15), "Больничный", false),
                Arguments.of(LocalDate.of(2024, 2, 20), "Отпуск", false),
                Arguments.of(LocalDate.of(2024, 3, 25), "Дополнительный рабочий день", true),
                Arguments.of(LocalDate.of(2024, 4, 10), "Конференция", false),
                Arguments.of(LocalDate.of(2024, 5, 5), "Экстренный прием", true)
        );
    }

    /**
     * Параметризованный тест для создания исключения расписания (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create schedule exception: {0}, reason: {1}")
    @MethodSource("provideScheduleExceptionDataForCreate")
    @DisplayName("save() should persist schedule exception with different data sets")
    void testSaveScheduleException_Parameterized(LocalDate exceptionDate, String reason, Boolean isWorkingDay) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason(reason)
                .isWorkingDay(isWorkingDay)
                .build();

        // Act
        ScheduleException saved = repository.save(exception);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getExceptionDate()).isEqualTo(exceptionDate);
        assertThat(saved.getReason()).isEqualTo(reason);
        assertThat(saved.getIsWorkingDay()).isEqualTo(isWorkingDay);
        assertThat(saved.getDoctor().getId()).isEqualTo(testDoctor1.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    /**
     * Параметризованный тест для чтения исключения расписания (READ)
     */
    @ParameterizedTest(name = "[{index}] Read schedule exception by ID: {1}")
    @MethodSource("provideScheduleExceptionDataForCreate")
    @DisplayName("findById() should retrieve schedule exception with different data sets")
    void testFindById_Parameterized(LocalDate exceptionDate, String reason, Boolean isWorkingDay) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason(reason)
                .isWorkingDay(isWorkingDay)
                .build();
        ScheduleException saved = repository.save(exception);

        // Act
        Optional<ScheduleException> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getExceptionDate()).isEqualTo(exceptionDate);
        assertThat(found.get().getReason()).isEqualTo(reason);
        assertThat(found.get().getIsWorkingDay()).isEqualTo(isWorkingDay);
    }

    /**
     * Источник данных для тестирования обновления исключений расписания
     */
    private static Stream<Arguments> provideScheduleExceptionDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2024, 1, 1), "Old reason 1", false,
                        LocalDate.of(2024, 1, 2), "New reason 1", true
                ),
                Arguments.of(
                        LocalDate.of(2024, 2, 1), "Old reason 2", true,
                        LocalDate.of(2024, 2, 2), "New reason 2", false
                ),
                Arguments.of(
                        LocalDate.of(2024, 3, 1), "Old reason 3", false,
                        LocalDate.of(2024, 3, 2), "New reason 3", true
                )
        );
    }

    /**
     * Параметризованный тест для обновления исключения расписания (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update schedule exception: {1} -> {4}")
    @MethodSource("provideScheduleExceptionDataForUpdate")
    @DisplayName("updateById() should update schedule exception fields with different data sets")
    void testUpdateById_Parameterized(
            LocalDate oldDate, String oldReason, Boolean oldIsWorking,
            LocalDate newDate, String newReason, Boolean newIsWorking) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(oldDate)
                .reason(oldReason)
                .isWorkingDay(oldIsWorking)
                .build();
        ScheduleException saved = repository.save(exception);
        UUID exceptionId = saved.getId();

        // Act
        int updated = repository.updateById(
                exceptionId,
                testDoctor2.getId(),
                newDate,
                newReason,
                newIsWorking
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<ScheduleException> updatedException = repository.findById(exceptionId);
        assertThat(updatedException).isPresent();
        assertThat(updatedException.get().getExceptionDate()).isEqualTo(newDate);
        assertThat(updatedException.get().getReason()).isEqualTo(newReason);
        assertThat(updatedException.get().getIsWorkingDay()).isEqualTo(newIsWorking);
    }

    /**
     * Параметризованный тест для удаления исключения расписания (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete schedule exception: {1}")
    @MethodSource("provideScheduleExceptionDataForCreate")
    @DisplayName("deleteById() should remove schedule exception with different data sets")
    void testDeleteById_Parameterized(LocalDate exceptionDate, String reason, Boolean isWorkingDay) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason(reason)
                .isWorkingDay(isWorkingDay)
                .build();
        ScheduleException saved = repository.save(exception);
        UUID exceptionId = saved.getId();

        assertThat(repository.findById(exceptionId)).isPresent();

        // Act
        repository.deleteById(exceptionId);

        // Assert
        assertThat(repository.findById(exceptionId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по ID доктора
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, Больничный, false",
            "2024-02-20, Отпуск, false",
            "2024-03-25, Дополнительный день, true"
    })
    @DisplayName("findByDoctorId() should find exceptions by doctor")
    void testFindByDoctorId_Parameterized(LocalDate exceptionDate, String reason, Boolean isWorkingDay) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason(reason)
                .isWorkingDay(isWorkingDay)
                .build();
        repository.save(exception);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<ScheduleException> found = repository.findByDoctorId(testDoctor1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(e -> e.getExceptionDate().equals(exceptionDate));
    }

    /**
     * Параметризованный тест для поиска по дате исключения
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, Больничный",
            "2024-02-20, Отпуск",
            "2024-03-25, Конференция"
    })
    @DisplayName("findByExceptionDate() should find exceptions by date")
    void testFindByExceptionDate_Parameterized(LocalDate exceptionDate, String reason) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason(reason)
                .isWorkingDay(false)
                .build();
        repository.save(exception);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<ScheduleException> found = repository.findByExceptionDate(exceptionDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(e -> e.getExceptionDate().equals(exceptionDate));
    }

    /**
     * Параметризованный тест для поиска по типу дня
     */
    @ParameterizedTest
    @CsvSource({
            "true, Дополнительный рабочий день, 2024-01-15",
            "false, Больничный, 2024-02-20"
    })
    @DisplayName("findByIsWorkingDay() should find exceptions by working day flag")
    void testFindByIsWorkingDay_Parameterized(Boolean isWorkingDay, String reason, LocalDate exceptionDate) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason(reason)
                .isWorkingDay(isWorkingDay)
                .build();
        repository.save(exception);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<ScheduleException> found = repository.findByIsWorkingDay(isWorkingDay, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(e -> e.getIsWorkingDay().equals(isWorkingDay));
    }

    /**
     * Параметризованный тест для поиска по ID доктора и дате
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, Больничный",
            "2024-02-20, Отпуск",
            "2024-03-25, Конференция"
    })
    @DisplayName("findByDoctorIdAndExceptionDate() should find exception by doctor and date")
    void testFindByDoctorIdAndExceptionDate_Parameterized(LocalDate exceptionDate, String reason) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason(reason)
                .isWorkingDay(false)
                .build();
        repository.save(exception);

        // Act
        Optional<ScheduleException> found = repository.findByDoctorIdAndExceptionDate(
                testDoctor1.getId(), 
                exceptionDate
        );

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getExceptionDate()).isEqualTo(exceptionDate);
        assertThat(found.get().getReason()).isEqualTo(reason);
    }

    /**
     * Параметризованный тест для поиска в диапазоне дат
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-01, 2024-01-31, 2024-01-15",
            "2024-02-01, 2024-02-28, 2024-02-20",
            "2024-03-01, 2024-03-31, 2024-03-25"
    })
    @DisplayName("findByExceptionDateBetween() should find exceptions in date range")
    void testFindByExceptionDateBetween_Parameterized(LocalDate startDate, LocalDate endDate, LocalDate exceptionDate) {
        // Arrange
        ScheduleException exception = ScheduleException.builder()
                .doctor(testDoctor1)
                .exceptionDate(exceptionDate)
                .reason("Test reason")
                .isWorkingDay(false)
                .build();
        repository.save(exception);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<ScheduleException> found = repository.findByExceptionDateBetween(startDate, endDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(e -> e.getExceptionDate().equals(exceptionDate));
    }
}

