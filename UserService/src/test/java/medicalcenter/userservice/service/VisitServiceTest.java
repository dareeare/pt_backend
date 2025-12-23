package medicalcenter.userservice.service;

import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.model.dto.visit.RescheduleVisitDto;
import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.TimeSlot;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.repository.TimeSlotRepository;
import medicalcenter.userservice.repository.VisitRepository;
import medicalcenter.userservice.service.impl.VisitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@Transactional
class VisitServiceTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private VisitService visitService;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Doctor testDoctor;
    private Patient testPatient;
    private Visit testVisit;
    private TimeSlot testTimeSlot;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        visitRepository.deleteAll();
        timeSlotRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();

        // Создание тестового врача
        testDoctor = Doctor.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .specialty("Кардиолог")
                .phone("80291234567")
                .email("ivanov@clinic.com")
                .build();
        testDoctor = doctorRepository.save(testDoctor);

        // Создание тестового пациента
        testPatient = Patient.builder()
                .firstName("Петр")
                .lastName("Сидоров")
                .phone("80293456789")
                .email("sidorov@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
        testPatient = patientRepository.save(testPatient);

        // Создание тестового визита
        LocalDateTime visitDateTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        testVisit = Visit.builder()
                .dateOfVisit(visitDateTime)
                .status("scheduled")
                .symptoms("Головная боль")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        testVisit = visitRepository.save(testVisit);

        // Создание тестового временного слота, связанного с визитом
        testTimeSlot = TimeSlot.builder()
                .doctor(testDoctor)
                .slotDate(visitDateTime.toLocalDate())
                .startTime(visitDateTime.toLocalTime())
                .endTime(visitDateTime.toLocalTime().plusMinutes(30))
                .visit(testVisit)
                .build();
        testTimeSlot = timeSlotRepository.save(testTimeSlot);
    }

    @Test
    void cancelVisit_ShouldCancelVisitAndReleaseSlot() {
        // Given
        UUID visitId = testVisit.getId();
        UUID slotId = testTimeSlot.getId();

        // Verify initial state
        Optional<TimeSlot> slotBefore = timeSlotRepository.findById(slotId);
        assertThat(slotBefore).isPresent();
        assertThat(slotBefore.get().getVisit()).isNotNull();
        assertThat(slotBefore.get().getVisit().getId()).isEqualTo(visitId);

        // When
        visitService.cancelVisit(visitId);

        // Then
        Optional<Visit> cancelledVisit = visitRepository.findById(visitId);
        assertThat(cancelledVisit).isPresent();
        assertThat(cancelledVisit.get().getStatus()).isEqualTo("cancelled");

        // Verify slot is released
        Optional<TimeSlot> slotAfter = timeSlotRepository.findById(slotId);
        assertThat(slotAfter).isPresent();
        assertThat(slotAfter.get().getVisit()).isNull();
    }

    @Test
    void cancelVisit_WhenVisitHasNoSlot_ShouldCancelVisitWithoutError() {
        // Given - создаем визит без слота
        Visit visitWithoutSlot = Visit.builder()
                .dateOfVisit(LocalDateTime.now().plusDays(2))
                .status("scheduled")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        visitWithoutSlot = visitRepository.save(visitWithoutSlot);
        UUID visitId = visitWithoutSlot.getId();

        // When
        visitService.cancelVisit(visitId);

        // Then
        Optional<Visit> cancelledVisit = visitRepository.findById(visitId);
        assertThat(cancelledVisit).isPresent();
        assertThat(cancelledVisit.get().getStatus()).isEqualTo("cancelled");
    }

    @Test
    void cancelVisit_WhenVisitNotFound_ShouldThrowNotFoundException() {
        // Given
        UUID nonExistentVisitId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> visitService.cancelVisit(nonExistentVisitId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void rescheduleVisit_ShouldMoveVisitToNewSlot() {
        // Given
        UUID visitId = testVisit.getId();
        UUID oldSlotId = testTimeSlot.getId();

        // Создаем новый свободный слот
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0);
        TimeSlot newSlot = TimeSlot.builder()
                .doctor(testDoctor)
                .slotDate(newDateTime.toLocalDate())
                .startTime(newDateTime.toLocalTime())
                .endTime(newDateTime.toLocalTime().plusMinutes(30))
                .visit(null) // свободный слот
                .build();
        newSlot = timeSlotRepository.save(newSlot);
        UUID newSlotId = newSlot.getId();

        RescheduleVisitDto rescheduleDto = new RescheduleVisitDto(
                newDateTime,
                testDoctor.getId(),
                null
        );

        // When
        visitService.rescheduleVisit(visitId, rescheduleDto);

        // Then
        Optional<Visit> rescheduledVisit = visitRepository.findById(visitId);
        assertThat(rescheduledVisit).isPresent();
        assertThat(rescheduledVisit.get().getDateOfVisit()).isEqualTo(newDateTime);

        // Старый слот должен быть освобожден
        Optional<TimeSlot> oldSlot = timeSlotRepository.findById(oldSlotId);
        assertThat(oldSlot).isPresent();
        assertThat(oldSlot.get().getVisit()).isNull();

        // Новый слот должен быть зарезервирован
        Optional<TimeSlot> updatedNewSlot = timeSlotRepository.findById(newSlotId);
        assertThat(updatedNewSlot).isPresent();
        assertThat(updatedNewSlot.get().getVisit()).isNotNull();
        assertThat(updatedNewSlot.get().getVisit().getId()).isEqualTo(visitId);
    }

    @Test
    void rescheduleVisit_WhenNewSlotIsAlreadyBooked_ShouldThrowException() {
        // Given
        UUID visitId = testVisit.getId();

        // Создаем занятый слот
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0);
        Visit anotherVisit = Visit.builder()
                .dateOfVisit(newDateTime)
                .status("scheduled")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        anotherVisit = visitRepository.save(anotherVisit);

        TimeSlot bookedSlot = TimeSlot.builder()
                .doctor(testDoctor)
                .slotDate(newDateTime.toLocalDate())
                .startTime(newDateTime.toLocalTime())
                .endTime(newDateTime.toLocalTime().plusMinutes(30))
                .visit(anotherVisit) // уже занят
                .build();
        bookedSlot = timeSlotRepository.save(bookedSlot);

        RescheduleVisitDto rescheduleDto = new RescheduleVisitDto(
                newDateTime,
                testDoctor.getId(),
                null
        );

        // When & Then
        assertThatThrownBy(() -> visitService.rescheduleVisit(visitId, rescheduleDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void rescheduleVisit_WhenNewSlotNotFound_ShouldThrowNotFoundException() {
        // Given
        UUID visitId = testVisit.getId();
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2).withHour(15).withMinute(0);

        RescheduleVisitDto rescheduleDto = new RescheduleVisitDto(
                newDateTime,
                testDoctor.getId(),
                null
        );

        // When & Then
        assertThatThrownBy(() -> visitService.rescheduleVisit(visitId, rescheduleDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Time slot not found");
    }

    @Test
    void rescheduleVisit_WhenVisitIsCompleted_ShouldThrowException() {
        // Given
        testVisit.setStatus("completed");
        testVisit = visitRepository.save(testVisit);
        UUID visitId = testVisit.getId();

        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0);
        TimeSlot newSlot = TimeSlot.builder()
                .doctor(testDoctor)
                .slotDate(newDateTime.toLocalDate())
                .startTime(newDateTime.toLocalTime())
                .endTime(newDateTime.toLocalTime().plusMinutes(30))
                .visit(null)
                .build();
        timeSlotRepository.save(newSlot);

        RescheduleVisitDto rescheduleDto = new RescheduleVisitDto(
                newDateTime,
                testDoctor.getId(),
                null
        );

        // When & Then
        assertThatThrownBy(() -> visitService.rescheduleVisit(visitId, rescheduleDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot reschedule a completed visit");
    }

    @Test
    void rescheduleVisit_WhenVisitNotFound_ShouldThrowNotFoundException() {
        // Given
        UUID nonExistentVisitId = UUID.randomUUID();
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0);

        RescheduleVisitDto rescheduleDto = new RescheduleVisitDto(
                newDateTime,
                testDoctor.getId(),
                null
        );

        // When & Then
        assertThatThrownBy(() -> visitService.rescheduleVisit(nonExistentVisitId, rescheduleDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void rescheduleVisit_WhenVisitHasNoSlot_ShouldRescheduleSuccessfully() {
        // Given - создаем визит без слота
        Visit visitWithoutSlot = Visit.builder()
                .dateOfVisit(LocalDateTime.now().plusDays(3))
                .status("scheduled")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        visitWithoutSlot = visitRepository.save(visitWithoutSlot);
        UUID visitId = visitWithoutSlot.getId();

        // Создаем новый свободный слот
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(4).withHour(16).withMinute(0);
        TimeSlot newSlot = TimeSlot.builder()
                .doctor(testDoctor)
                .slotDate(newDateTime.toLocalDate())
                .startTime(newDateTime.toLocalTime())
                .endTime(newDateTime.toLocalTime().plusMinutes(30))
                .visit(null)
                .build();
        newSlot = timeSlotRepository.save(newSlot);
        UUID newSlotId = newSlot.getId();

        RescheduleVisitDto rescheduleDto = new RescheduleVisitDto(
                newDateTime,
                testDoctor.getId(),
                null
        );

        // When
        visitService.rescheduleVisit(visitId, rescheduleDto);

        // Then
        Optional<Visit> rescheduledVisit = visitRepository.findById(visitId);
        assertThat(rescheduledVisit).isPresent();
        assertThat(rescheduledVisit.get().getDateOfVisit()).isEqualTo(newDateTime);

        // Новый слот должен быть зарезервирован
        Optional<TimeSlot> updatedNewSlot = timeSlotRepository.findById(newSlotId);
        assertThat(updatedNewSlot).isPresent();
        assertThat(updatedNewSlot.get().getVisit()).isNotNull();
        assertThat(updatedNewSlot.get().getVisit().getId()).isEqualTo(visitId);
    }
}

