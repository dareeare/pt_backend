package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.TimeSlot;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для TimeSlotRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class TimeSlotRepositoryParameterizedTests {

    @Autowired
    private TimeSlotRepository repository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Doctor testDoctor1;
    private Doctor testDoctor2;
    private Patient testPatient;
    private Visit testVisit;

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

        testPatient = Patient.builder()
                .firstName("Петр")
                .lastName("Сидоров")
                .phone("80293456789")
                .email("sidorov@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
        testPatient = patientRepository.save(testPatient);

        testVisit = Visit.builder()
                .dateOfVisit(LocalDateTime.of(2024, 1, 15, 10, 0))
                .status("scheduled")
                .symptoms("Test symptoms")
                .patient(testPatient)
                .doctor(testDoctor1)
                .build();
        testVisit = visitRepository.save(testVisit);
    }

    /**
     * Источник данных для тестирования создания временных слотов
     */
    private static Stream<Arguments> provideTimeSlotDataForCreate() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 1, 15), LocalTime.of(9, 0), LocalTime.of(9, 30)),
                Arguments.of(LocalDate.of(2024, 2, 20), LocalTime.of(10, 0), LocalTime.of(10, 30)),
                Arguments.of(LocalDate.of(2024, 3, 25), LocalTime.of(14, 0), LocalTime.of(14, 30)),
                Arguments.of(LocalDate.of(2024, 4, 10), LocalTime.of(15, 30), LocalTime.of(16, 0)),
                Arguments.of(LocalDate.of(2024, 5, 5), LocalTime.of(11, 0), LocalTime.of(11, 30))
        );
    }

    /**
     * Параметризованный тест для создания временного слота (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create time slot: {0} {1}-{2}")
    @MethodSource("provideTimeSlotDataForCreate")
    @DisplayName("save() should persist time slot with different data sets")
    void testSaveTimeSlot_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();

        // Act
        TimeSlot saved = repository.save(timeSlot);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSlotDate()).isEqualTo(slotDate);
        assertThat(saved.getStartTime()).isEqualTo(startTime);
        assertThat(saved.getEndTime()).isEqualTo(endTime);
        assertThat(saved.getDoctor().getId()).isEqualTo(testDoctor1.getId());
        assertThat(saved.getVisit()).isNull();
    }

    /**
     * Параметризованный тест для чтения временного слота (READ)
     */
    @ParameterizedTest(name = "[{index}] Read time slot by ID: {0}")
    @MethodSource("provideTimeSlotDataForCreate")
    @DisplayName("findById() should retrieve time slot with different data sets")
    void testFindById_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        TimeSlot saved = repository.save(timeSlot);

        // Act
        Optional<TimeSlot> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getSlotDate()).isEqualTo(slotDate);
        assertThat(found.get().getStartTime()).isEqualTo(startTime);
        assertThat(found.get().getEndTime()).isEqualTo(endTime);
    }

    /**
     * Источник данных для тестирования обновления временных слотов
     */
    private static Stream<Arguments> provideTimeSlotDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2024, 1, 1), LocalTime.of(9, 0), LocalTime.of(9, 30),
                        LocalDate.of(2024, 1, 2), LocalTime.of(10, 0), LocalTime.of(10, 30)
                ),
                Arguments.of(
                        LocalDate.of(2024, 2, 1), LocalTime.of(14, 0), LocalTime.of(14, 30),
                        LocalDate.of(2024, 2, 2), LocalTime.of(15, 0), LocalTime.of(15, 30)
                ),
                Arguments.of(
                        LocalDate.of(2024, 3, 1), LocalTime.of(11, 0), LocalTime.of(11, 30),
                        LocalDate.of(2024, 3, 2), LocalTime.of(12, 0), LocalTime.of(12, 30)
                )
        );
    }

    /**
     * Параметризованный тест для обновления временного слота (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update time slot: {0} {1} -> {3} {4}")
    @MethodSource("provideTimeSlotDataForUpdate")
    @DisplayName("updateById() should update time slot fields with different data sets")
    void testUpdateById_Parameterized(
            LocalDate oldDate, LocalTime oldStart, LocalTime oldEnd,
            LocalDate newDate, LocalTime newStart, LocalTime newEnd) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(oldDate)
                .startTime(oldStart)
                .endTime(oldEnd)
                .visit(null)
                .build();
        TimeSlot saved = repository.save(timeSlot);
        UUID timeSlotId = saved.getId();

        // Act
        int updated = repository.updateById(
                timeSlotId,
                testDoctor2.getId(),
                newDate,
                newStart,
                newEnd,
                testVisit
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<TimeSlot> updatedSlot = repository.findById(timeSlotId);
        assertThat(updatedSlot).isPresent();
        assertThat(updatedSlot.get().getSlotDate()).isEqualTo(newDate);
        assertThat(updatedSlot.get().getStartTime()).isEqualTo(newStart);
        assertThat(updatedSlot.get().getEndTime()).isEqualTo(newEnd);
    }

    /**
     * Параметризованный тест для удаления временного слота (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete time slot: {0}")
    @MethodSource("provideTimeSlotDataForCreate")
    @DisplayName("deleteById() should remove time slot with different data sets")
    void testDeleteById_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        TimeSlot saved = repository.save(timeSlot);
        UUID timeSlotId = saved.getId();

        assertThat(repository.findById(timeSlotId)).isPresent();

        // Act
        repository.deleteById(timeSlotId);

        // Assert
        assertThat(repository.findById(timeSlotId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по ID доктора
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, 09:00, 09:30",
            "2024-02-20, 10:00, 10:30",
            "2024-03-25, 14:00, 14:30"
    })
    @DisplayName("findByDoctorId() should find time slots by doctor")
    void testFindByDoctorId_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        repository.save(timeSlot);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<TimeSlot> found = repository.findByDoctorId(testDoctor1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(ts -> ts.getSlotDate().equals(slotDate));
    }

    /**
     * Параметризованный тест для поиска по дате слота
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, 09:00, 09:30",
            "2024-02-20, 10:00, 10:30",
            "2024-03-25, 14:00, 14:30"
    })
    @DisplayName("findBySlotDate() should find time slots by date")
    void testFindBySlotDate_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        repository.save(timeSlot);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<TimeSlot> found = repository.findBySlotDate(slotDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(ts -> ts.getSlotDate().equals(slotDate));
    }

    /**
     * Параметризованный тест для поиска доступных слотов
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, 09:00, 09:30",
            "2024-02-20, 10:00, 10:30",
            "2024-03-25, 14:00, 14:30"
    })
    @DisplayName("findAvailableSlots() should find available time slots")
    void testFindAvailableSlots_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        repository.save(timeSlot);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<TimeSlot> found = repository.findAvailableSlots(pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(ts -> ts.getVisit() == null);
    }

    /**
     * Параметризованный тест для поиска доступных слотов по ID доктора
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, 09:00, 09:30",
            "2024-02-20, 10:00, 10:30",
            "2024-03-25, 14:00, 14:30"
    })
    @DisplayName("findAvailableSlotsByDoctorId() should find available slots for doctor")
    void testFindAvailableSlotsByDoctorId_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        repository.save(timeSlot);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<TimeSlot> found = repository.findAvailableSlotsByDoctorId(testDoctor1.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(ts -> ts.getVisit() == null && ts.getSlotDate().equals(slotDate));
    }

    /**
     * Параметризованный тест для поиска по доктору и дате
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, 09:00, 09:30",
            "2024-02-20, 10:00, 10:30",
            "2024-03-25, 14:00, 14:30"
    })
    @DisplayName("findByDoctorIdAndSlotDate() should find slots by doctor and date")
    void testFindByDoctorIdAndSlotDate_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        repository.save(timeSlot);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<TimeSlot> found = repository.findByDoctorIdAndSlotDate(testDoctor1.getId(), slotDate, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(ts -> 
                ts.getSlotDate().equals(slotDate) && 
                ts.getDoctor().getId().equals(testDoctor1.getId())
        );
    }

    /**
     * Параметризованный тест для поиска по доктору, дате и времени
     */
    @ParameterizedTest
    @CsvSource({
            "2024-01-15, 09:00, 09:30",
            "2024-02-20, 10:00, 10:30",
            "2024-03-25, 14:00, 14:30"
    })
    @DisplayName("findByDoctorIdAndSlotDateAndStartTime() should find slot by doctor, date and start time")
    void testFindByDoctorIdAndSlotDateAndStartTime_Parameterized(LocalDate slotDate, LocalTime startTime, LocalTime endTime) {
        // Arrange
        TimeSlot timeSlot = TimeSlot.builder()
                .doctor(testDoctor1)
                .slotDate(slotDate)
                .startTime(startTime)
                .endTime(endTime)
                .visit(null)
                .build();
        repository.save(timeSlot);

        // Act
        Optional<TimeSlot> found = repository.findByDoctorIdAndSlotDateAndStartTime(
                testDoctor1.getId(), 
                slotDate, 
                startTime
        );

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getSlotDate()).isEqualTo(slotDate);
        assertThat(found.get().getStartTime()).isEqualTo(startTime);
    }
}

