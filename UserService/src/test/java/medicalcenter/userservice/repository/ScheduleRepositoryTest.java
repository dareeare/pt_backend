package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Schedule;
import medicalcenter.userservice.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ScheduleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ScheduleRepository scheduleRepository;

    private Doctor doctor1;
    private Doctor doctor2;
    private Schedule schedule1;
    private Schedule schedule2;
    private Schedule schedule3;
    private Schedule schedule4;

    @BeforeEach
    void setUp() {
        doctor1 = Doctor.builder()
                .firstName("Иван")
                .lastName("Петров")
                .phone("+79991112233")
                .email("ivan.petrov@example.com")
                .specialty("Терапевт")
                .build();

        doctor2 = Doctor.builder()
                .firstName("Мария")
                .lastName("Сидорова")
                .phone("+79994445566")
                .email("maria.sidorova@example.com")
                .specialty("Кардиолог")
                .build();

        entityManager.persist(doctor1);
        entityManager.persist(doctor2);
        entityManager.flush();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        schedule1 = Schedule.builder()
                .startTime(LocalDateTime.of(today, java.time.LocalTime.of(9, 0)))
                .endTime(LocalDateTime.of(today, java.time.LocalTime.of(18, 0)))
                .workDay(today)
                .doctor(doctor1)
                .build();

        schedule2 = Schedule.builder()
                .startTime(LocalDateTime.of(tomorrow, java.time.LocalTime.of(10, 0)))
                .endTime(LocalDateTime.of(tomorrow, java.time.LocalTime.of(19, 0)))
                .workDay(tomorrow)
                .doctor(doctor1)
                .build();

        schedule3 = Schedule.builder()
                .startTime(LocalDateTime.of(today, java.time.LocalTime.of(8, 0)))
                .endTime(LocalDateTime.of(today, java.time.LocalTime.of(17, 0)))
                .workDay(today)
                .doctor(doctor2)
                .build();

        schedule4 = Schedule.builder()
                .startTime(LocalDateTime.of(yesterday, java.time.LocalTime.of(9, 0)))
                .endTime(LocalDateTime.of(yesterday, java.time.LocalTime.of(16, 0)))
                .workDay(yesterday)
                .doctor(doctor2)
                .build();

        entityManager.persist(schedule1);
        entityManager.persist(schedule2);
        entityManager.persist(schedule3);
        entityManager.persist(schedule4);
        entityManager.flush();
    }

    @Test
    void findByDoctorLastNameContainingIgnoreCase_shouldReturnMatchingSchedules() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "петров";

        List<Schedule> result = scheduleRepository.findByDoctorLastNameContainingIgnoreCase(lastName, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(schedule -> schedule.getDoctor().getLastName())
                .allMatch(name -> name.equals("Петров"));
    }

    @Test
    void findByDoctorLastNameContainingIgnoreCase_shouldBeCaseInsensitive() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "СИДОРОВА";

        List<Schedule> result = scheduleRepository.findByDoctorLastNameContainingIgnoreCase(lastName, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(schedule -> schedule.getDoctor().getLastName())
                .allMatch(name -> name.equals("Сидорова"));
    }

    @Test
    void findByDoctorLastNameAndDoctorFirstName_shouldReturnExactMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Schedule> result = scheduleRepository.findByDoctorLastNameAndDoctorFirstName(
                "Петров", "Иван", pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(schedule -> schedule.getDoctor().getLastName())
                .allMatch(name -> name.equals("Петров"));
        assertThat(result).extracting(schedule -> schedule.getDoctor().getFirstName())
                .allMatch(name -> name.equals("Иван"));
    }

    @Test
    void findByDoctorId_shouldReturnSchedulesForDoctor() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Schedule> result = scheduleRepository.findByDoctorId(doctor1.getId(), pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Schedule::getDoctor)
                .allMatch(doctor -> doctor.getId().equals(doctor1.getId()));
    }

    @Test
    void findByDoctorId_shouldReturnEmptyListForNonExistingDoctor() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID nonExistingDoctorId = UUID.randomUUID();

        List<Schedule> result = scheduleRepository.findByDoctorId(nonExistingDoctorId, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByWorkDay_shouldReturnSchedulesForSpecificDay() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate today = LocalDate.now();

        List<Schedule> result = scheduleRepository.findByWorkDay(today, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Schedule::getWorkDay)
                .allMatch(day -> day.equals(today));
    }

    @Test
    void findByDoctorIdAndWorkDay_shouldReturnSpecificSchedule() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate today = LocalDate.now();

        List<Schedule> result = scheduleRepository.findByDoctorIdAndWorkDay(doctor1.getId(), today, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDoctor().getId()).isEqualTo(doctor1.getId());
        assertThat(result.get(0).getWorkDay()).isEqualTo(today);
    }

    @Test
    void findByWorkDayBetween_shouldReturnSchedulesInDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        List<Schedule> result = scheduleRepository.findByWorkDayBetween(startDate, endDate, pageable);

        assertThat(result).hasSize(4);
        assertThat(result).extracting(Schedule::getWorkDay)
                .allMatch(day -> !day.isBefore(startDate) && !day.isAfter(endDate));
    }

    @Test
    void findByWorkDayBetween_shouldReturnSchedulesForSpecificRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        List<Schedule> result = scheduleRepository.findByWorkDayBetween(startDate, endDate, pageable);

        assertThat(result).hasSize(3);
    }

    @Test
    void updateById_shouldReturnZeroForNonExistingId() {
        UUID nonExistingId = UUID.randomUUID();

        int updatedCount = scheduleRepository.updateById(
                nonExistingId,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(8),
                LocalDate.now(),
                doctor1.getId()
        );

        assertThat(updatedCount).isEqualTo(0);
    }

    @Test
    void findAll_shouldReturnAllSchedules() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Schedule> result = scheduleRepository.findAll(pageable).getContent();

        assertThat(result).hasSize(4);
    }

    @Test
    void findById_shouldReturnSchedule() {
        Schedule result = scheduleRepository.findById(schedule1.getId()).orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getDoctor().getLastName()).isEqualTo("Петров");
    }

    @Test
    void save_shouldPersistNewSchedule() {
        Schedule newSchedule = Schedule.builder()
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(3).plusHours(8))
                .workDay(LocalDate.now().plusDays(3))
                .doctor(doctor1)
                .build();

        Schedule saved = scheduleRepository.save(newSchedule);

        assertThat(saved.getId()).isNotNull();
        assertThat(entityManager.find(Schedule.class, saved.getId())).isNotNull();
    }

    @Test
    void delete_shouldRemoveSchedule() {
        scheduleRepository.delete(schedule1);

        assertThat(entityManager.find(Schedule.class, schedule1.getId())).isNull();
    }

    @Test
    void findByDoctorLastNameContainingIgnoreCase_shouldReturnEmptyForNonMatchingName() {
        Pageable pageable = PageRequest.of(0, 10);
        String nonMatchingLastName = "НесуществующаяФамилия";

        List<Schedule> result = scheduleRepository.findByDoctorLastNameContainingIgnoreCase(nonMatchingLastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByWorkDay_shouldReturnEmptyForNonExistingDate() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate nonExistingDate = LocalDate.now().plusYears(1);

        List<Schedule> result = scheduleRepository.findByWorkDay(nonExistingDate, pageable);

        assertThat(result).isEmpty();
    }
}