package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.*;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TimeSlotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private Doctor doctor1;
    private Doctor doctor2;
    private Patient patient;
    private Visit visit1;
    private Visit visit2;
    private TimeSlot slot1;
    private TimeSlot slot2;
    private TimeSlot slot3;
    private TimeSlot slot4;

    @BeforeEach
    void setUp() {
        doctor1 = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+375449876543")
                .email("john.doe@example.com")
                .specialty("Cardiology")
                .build();

        doctor2 = Doctor.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phone("+375256876543")
                .email("jane.smith@example.com")
                .specialty("Dermatology")
                .build();

        patient = Patient.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .phone("+375335739128")
                .email("mike.johnson@example.com")
                .build();

        entityManager.persist(doctor1);
        entityManager.persist(doctor2);
        entityManager.persist(patient);
        entityManager.flush();

        visit1 = Visit.builder()
                .doctor(doctor1)
                .patient(patient)
                .dateOfVisit(LocalDateTime.now())
                .diagnosis("Common cold")
                .symptoms("Cough, fever")
                .status("scheduled")
                .build();

        visit2 = Visit.builder()
                .doctor(doctor1)
                .patient(patient)
                .dateOfVisit(LocalDateTime.now().plusDays(1))
                .diagnosis("Headache")
                .symptoms("Head pain")
                .status("scheduled")
                .build();

        entityManager.persist(visit1);
        entityManager.persist(visit2);
        entityManager.flush();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        slot1 = TimeSlot.builder()
                .doctor(doctor1)
                .slotDate(today)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 30))
                .visit(null)
                .build();

        slot2 = TimeSlot.builder()
                .doctor(doctor1)
                .slotDate(today)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 30))
                .visit(visit1)
                .build();

        slot3 = TimeSlot.builder()
                .doctor(doctor2)
                .slotDate(tomorrow)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(11, 30))
                .visit(null)
                .build();

        slot4 = TimeSlot.builder()
                .doctor(doctor1)
                .slotDate(tomorrow)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(14, 30))
                .visit(visit2)
                .build();

        entityManager.persist(slot1);
        entityManager.persist(slot2);
        entityManager.persist(slot3);
        entityManager.persist(slot4);
        entityManager.flush();
    }

    @Test
    void findByDoctorId_shouldReturnSlotsForDoctor() {
        Pageable pageable = PageRequest.of(0, 10);

        List<TimeSlot> result = timeSlotRepository.findByDoctorId(doctor1.getId(), pageable);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(TimeSlot::getDoctor)
                .allMatch(d -> d.getId().equals(doctor1.getId()));
    }

    @Test
    void findBySlotDate_shouldReturnSlotsForDate() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate today = LocalDate.now();

        List<TimeSlot> result = timeSlotRepository.findBySlotDate(today, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TimeSlot::getSlotDate)
                .allMatch(date -> date.equals(today));
    }

    @Test
    void findByDoctorIdAndSlotDate_shouldReturnSlotsForDoctorAndDate() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate today = LocalDate.now();

        List<TimeSlot> result = timeSlotRepository.findByDoctorIdAndSlotDate(doctor1.getId(), today, pageable);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDoctor().getId()).isEqualTo(doctor1.getId());
        assertThat(result.get(0).getSlotDate()).isEqualTo(today);
    }

    @Test
    void findAvailableSlots_shouldReturnOnlyAvailableSlots() {
        Pageable pageable = PageRequest.of(0, 10);

        List<TimeSlot> result = timeSlotRepository.findAvailableSlots(pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TimeSlot::getVisit)
                .allMatch(v -> v == null);
    }

    @Test
    void findAvailableSlotsByDoctorId_shouldReturnAvailableSlotsForDoctor() {
        Pageable pageable = PageRequest.of(0, 10);

        List<TimeSlot> result = timeSlotRepository.findAvailableSlotsByDoctorId(doctor1.getId(), pageable);

        assertThat(result).hasSize(1);
        assertThat(result).extracting(TimeSlot::getDoctor)
                .allMatch(d -> d.getId().equals(doctor1.getId()));
        assertThat(result).extracting(TimeSlot::getVisit)
                .allMatch(v -> v == null);
    }

    @Test
    void findAvailableSlotsByDoctorIdAndDate_shouldReturnAvailableSlotsForDoctorAndDate() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<TimeSlot> result = timeSlotRepository.findAvailableSlotsByDoctorIdAndDate(doctor2.getId(), tomorrow, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDoctor().getId()).isEqualTo(doctor2.getId());
        assertThat(result.get(0).getSlotDate()).isEqualTo(tomorrow);
        assertThat(result.get(0).getVisit()).isNull();
    }

    @Test
    void findByDoctorIdAndSlotDateAndStartTime_shouldReturnSpecificSlot() {
        LocalDate today = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);

        Optional<TimeSlot> result = timeSlotRepository.findByDoctorIdAndSlotDateAndStartTime(doctor1.getId(), today, startTime);

        assertThat(result).isPresent();
        assertThat(result.get().getDoctor().getId()).isEqualTo(doctor1.getId());
        assertThat(result.get().getSlotDate()).isEqualTo(today);
        assertThat(result.get().getStartTime()).isEqualTo(startTime);
    }
}