package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Schedule;
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
 * Параметризованные тесты для ScheduleRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ScheduleRepositoryParameterizedTests {

    @Autowired
    private ScheduleRepository repository;

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
     * Источник данных для тестирования создания расписаний
     */
    private static Stream<Arguments> provideScheduleDataForCreate() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2024, 1, 15, 9, 0),
                        LocalDateTime.of(2024, 1, 15, 18, 0),
                        LocalDate.of(2024, 1, 15)
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 2, 20, 10, 0),
                        LocalDateTime.of(2024, 2, 20, 19, 0),
                        LocalDate.of(2024, 2, 20)
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 3, 25, 8, 0),
                        LocalDateTime.of(2024, 3, 25, 17, 0),
                        LocalDate.of(2024, 3, 25)
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 4, 10, 11, 0),
                        LocalDateTime.of(2024, 4, 10, 20, 0),
                        LocalDate.of(2024, 4, 10)
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 5, 5, 9, 30),
                        LocalDateTime.of(2024, 5, 5, 18, 30),
                        LocalDate.of(2024, 5, 5)
                )
        );
    }

    /**
     * Параметризованный тест для создания расписания (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create schedule: {2}")
    @MethodSource("provideScheduleDataForCreate")
    @DisplayName("save() should persist schedule with different data sets")
    void testSaveSchedule_Parameterized(LocalDateTime startTime, LocalDateTime endTime, LocalDate workDay) {
        // Arrange
        Schedule schedule = Schedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .workDay(workDay)
                .doctor(testDoctor1)
                .build();

        // Act
        Schedule saved = repository.save(schedule);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStartTime()).isEqualTo(startTime);
        assertThat(saved.getEndTime()).isEqualTo(endTime);
        assertThat(saved.getWorkDay()).isEqualTo(workDay);
        assertThat(saved.getDoctor().getId()).isEqualTo(testDoctor1.getId());
    }

    /**
     * Параметризованный тест для чтения расписания (READ)
     */
    @ParameterizedTest(name = "[{index}] Read schedule by ID: {2}")
    @MethodSource("provideScheduleDataForCreate")
    @DisplayName("findById() should retrieve schedule with different data sets")
    void testFindById_Parameterized(LocalDateTime startTime, LocalDateTime endTime, LocalDate workDay) {
        // Arrange
        Schedule schedule = Schedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .workDay(workDay)
                .doctor(testDoctor1)
                .build();
        Schedule saved = repository.save(schedule);

        // Act
        Optional<Schedule> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getStartTime()).isEqualTo(startTime);
        assertThat(found.get().getEndTime()).isEqualTo(endTime);
        assertThat(found.get().getWorkDay()).isEqualTo(workDay);
    }

    /**
     * Источник данных для тестирования обновления расписаний
     */
    private static Stream<Arguments> provideScheduleDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2024, 1, 1, 9, 0), LocalDateTime.of(2024, 1, 1, 17, 0), LocalDate.of(2024, 1, 1),
                        LocalDateTime.of(2024, 1, 1, 10, 0), LocalDateTime.of(2024, 1, 1, 18, 0), LocalDate.of(2024, 1, 1)
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 2, 1, 8, 0), LocalDateTime.of(2024, 2, 1, 16, 0), LocalDate.of(2024, 2, 1),
                        LocalDateTime.of(2024, 2, 1, 11, 0), LocalDateTime.of(2024, 2, 1, 19, 0), LocalDate.of(2024, 2, 1)
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 3, 1, 10, 0), LocalDateTime.of(2024, 3, 1, 18, 0), LocalDate.of(2024, 3, 1),
                        LocalDateTime.of(2024, 3, 1, 9, 0), LocalDateTime.of(2024, 3, 1, 20, 0), LocalDate.of(2024, 3, 1)
                )
        );
    }

    /**
     * Параметризованный тест для обновления расписания (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update schedule: {2} -> {5}")
    @MethodSource("provideScheduleDataForUpdate")
    @DisplayName("updateById() should update schedule fields with different data sets")
    void testUpdateById_Parameterized(
            LocalDateTime oldStart, LocalDateTime oldEnd, LocalDate oldDay,
            LocalDateTime newStart, LocalDateTime newEnd, LocalDate newDay) {
        // Arrange
        Schedule schedule = Schedule.builder()
                .startTime(oldStart)
                .endTime(oldEnd)
                .workDay(oldDay)
                .doctor(testDoctor1)
                .build();
        Schedule saved = repository.save(schedule);
        UUID scheduleId = saved.getId();

        // Act
        int updated = repository.updateById(
                scheduleId,
                newStart,
                newEnd,
                newDay,
                testDoctor2.getId()
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<Schedule> updatedSchedule = repository.findById(scheduleId);
        assertThat(updatedSchedule).isPresent();
        assertThat(updatedSchedule.get().getStartTime()).isEqualTo(newStart);
        assertThat(updatedSchedule.get().getEndTime()).isEqualTo(newEnd);
        assertThat(updatedSchedule.get().getWorkDay()).isEqualTo(newDay);
    }

    /**
     * Параметризованный тест для удаления расписания (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete schedule: {2}")
    @MethodSource("provideScheduleDataForCreate")
    @DisplayName("deleteById() should remove schedule with different data sets")
    void testDeleteById_Parameterized(LocalDateTime startTime, LocalDateTime endTime, LocalDate workDay) {
        // Arrange
        Schedule schedule = Schedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .workDay(workDay)
                .doctor(testDoctor1)
                .build();
        Schedule saved = repository.save(schedule);
        UUID scheduleId = saved.getId();

        assertThat(repository.findById(scheduleId)).isPresent();

        // Act
        repository.deleteById(scheduleId);

        // Assert
        assertThat(repository.findById(scheduleId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по ID доктора
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, 09:00, 18:00",
            "2024-02-20, 10:00, 19:00",
            "2024-03-25, 08:00, 17:00"
    })
    @DisplayName("findByDoctorId() should find schedules by doctor")
    void testFindByDoctorId_Parameterized(LocalDate workDay, String startTimeStr, String endTimeStr) {
        // Arrange
        LocalDateTime startTime = LocalDateTime.of(workDay.getYear(), workDay.getMonth(), workDay.getDayOfMonth(), 
                                                   Integer.parseInt(startTimeStr.split(":")[0]), 
                                                   Integer.parseInt(startTimeStr.split(":")[1]));
        LocalDateTime endTime = LocalDateTime.of(workDay.getYear(), workDay.getMonth(), workDay.getDayOfMonth(), 
                                                 Integer.parseInt(endTimeStr.split(":")[0]), 
                                                 Integer.parseInt(endTimeStr.split(":")[1]));
        
        Schedule schedule = Schedule.builder()
                .startTime(startTime)
                .endTime(endTime)
                .workDay(workDay)
                .doctor(testDoctor1)
                .build();
        repository.save(schedule);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Schedule> found = repository.findByDoctorId(testDoctor1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(s -> s.getWorkDay().equals(workDay));
    }

    /**
     * Параметризованный тест для поиска по дню работы
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15",
            "2024-02-20",
            "2024-03-25"
    })
    @DisplayName("findByWorkDay() should find schedules by work day")
    void testFindByWorkDay_Parameterized(LocalDate workDay) {
        // Arrange
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.of(workDay.getYear(), workDay.getMonth(), workDay.getDayOfMonth(), 9, 0))
                .endTime(LocalDateTime.of(workDay.getYear(), workDay.getMonth(), workDay.getDayOfMonth(), 18, 0))
                .workDay(workDay)
                .doctor(testDoctor1)
                .build();
        repository.save(schedule);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Schedule> found = repository.findByWorkDay(workDay, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(s -> s.getWorkDay().equals(workDay));
    }

    /**
     * Параметризованный тест для поиска по ID доктора и дню
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15",
            "2024-02-20",
            "2024-03-25"
    })
    @DisplayName("findByDoctorIdAndWorkDay() should find schedules by doctor and work day")
    void testFindByDoctorIdAndWorkDay_Parameterized(LocalDate workDay) {
        // Arrange
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.of(workDay.getYear(), workDay.getMonth(), workDay.getDayOfMonth(), 9, 0))
                .endTime(LocalDateTime.of(workDay.getYear(), workDay.getMonth(), workDay.getDayOfMonth(), 18, 0))
                .workDay(workDay)
                .doctor(testDoctor1)
                .build();
        repository.save(schedule);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Schedule> found = repository.findByDoctorIdAndWorkDay(testDoctor1.getId(), workDay, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(s -> s.getWorkDay().equals(workDay));
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
    @DisplayName("findByWorkDayBetween() should find schedules in date range")
    void testFindByWorkDayBetween_Parameterized(LocalDate startDate, LocalDate endDate, LocalDate scheduleDate) {
        // Arrange
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.of(scheduleDate.getYear(), scheduleDate.getMonth(), scheduleDate.getDayOfMonth(), 9, 0))
                .endTime(LocalDateTime.of(scheduleDate.getYear(), scheduleDate.getMonth(), scheduleDate.getDayOfMonth(), 18, 0))
                .workDay(scheduleDate)
                .doctor(testDoctor1)
                .build();
        repository.save(schedule);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Schedule> found = repository.findByWorkDayBetween(startDate, endDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(s -> s.getWorkDay().equals(scheduleDate));
    }
}

