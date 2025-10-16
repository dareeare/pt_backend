package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.ScheduleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ScheduleExceptionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ScheduleExceptionRepository scheduleExceptionRepository;

    private Doctor doctor1;
    private Doctor doctor2;
    private ScheduleException exception1;
    private ScheduleException exception2;
    private ScheduleException exception3;

    @BeforeEach
    void setUp() {
        doctor1 = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+375251234567")
                .email("john.doe@example.com")
                .specialty("Cardiology")
                .build();

        doctor2 = Doctor.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phone("+375449876543")
                .email("jane.smith@example.com")
                .specialty("Dermatology")
                .build();

        entityManager.persist(doctor1);
        entityManager.persist(doctor2);
        entityManager.flush();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        exception1 = ScheduleException.builder()
                .doctor(doctor1)
                .exceptionDate(today)
                .reason("Vacation")
                .isWorkingDay(false)
                .build();

        exception2 = ScheduleException.builder()
                .doctor(doctor1)
                .exceptionDate(tomorrow)
                .reason("Emergency")
                .isWorkingDay(false)
                .build();

        exception3 = ScheduleException.builder()
                .doctor(doctor2)
                .exceptionDate(yesterday)
                .reason("Extra shift")
                .isWorkingDay(true)
                .build();

        entityManager.persist(exception1);
        entityManager.persist(exception2);
        entityManager.persist(exception3);
        entityManager.flush();
    }

    @Test
    void findByDoctorId_shouldReturnExceptionsForDoctor() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ScheduleException> result = scheduleExceptionRepository.findByDoctorId(doctor1.getId(), pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ScheduleException::getDoctor)
                .allMatch(d -> d.getId().equals(doctor1.getId()));
    }

    @Test
    void findByExceptionDate_shouldReturnExceptionsForDate() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate today = LocalDate.now();

        List<ScheduleException> result = scheduleExceptionRepository.findByExceptionDate(today, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExceptionDate()).isEqualTo(today);
    }

    @Test
    void findByDoctorIdAndExceptionDate_shouldReturnSpecificException() {
        LocalDate today = LocalDate.now();

        Optional<ScheduleException> result = scheduleExceptionRepository.findByDoctorIdAndExceptionDate(doctor1.getId(), today);

        assertThat(result).isPresent();
        assertThat(result.get().getDoctor().getId()).isEqualTo(doctor1.getId());
        assertThat(result.get().getExceptionDate()).isEqualTo(today);
    }

    @Test
    void findByIsWorkingDay_shouldReturnWorkingDayExceptions() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ScheduleException> result = scheduleExceptionRepository.findByIsWorkingDay(true, pageable);

        assertThat(result).hasSize(1);
        assertThat(result).extracting(ScheduleException::getIsWorkingDay)
                .allMatch(Boolean.TRUE::equals);
    }

    @Test
    void findByExceptionDateBetween_shouldReturnExceptionsInDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        List<ScheduleException> result = scheduleExceptionRepository.findByExceptionDateBetween(startDate, endDate, pageable);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(ScheduleException::getExceptionDate)
                .allMatch(date -> !date.isBefore(startDate) && !date.isAfter(endDate));
    }

    @Test
    void updateById_shouldUpdateException() {
        UUID newDoctorId = doctor2.getId();
        LocalDate newExceptionDate = LocalDate.now().plusDays(5);
        String newReason = "Updated reason";
        Boolean newIsWorkingDay = true;

        int updatedCount = scheduleExceptionRepository.updateById(
                exception1.getId(),
                newDoctorId,
                newExceptionDate,
                newReason,
                newIsWorkingDay
        );

        assertThat(updatedCount).isEqualTo(1);

        entityManager.clear();
        ScheduleException updatedException = entityManager.find(ScheduleException.class, exception1.getId());
        
        assertThat(updatedException.getDoctor().getId()).isEqualTo(newDoctorId);
        assertThat(updatedException.getExceptionDate()).isEqualTo(newExceptionDate);
        assertThat(updatedException.getReason()).isEqualTo(newReason);
        assertThat(updatedException.getIsWorkingDay()).isEqualTo(newIsWorkingDay);
    }
}