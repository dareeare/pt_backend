package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
class VisitRepositoryTest {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private Doctor doctor1;
    private Doctor doctor2;
    private Patient patient1;
    private Patient patient2;
    private Patient patient3;
    private Visit visit1;
    private Visit visit2;
    private Visit visit3;
    private Visit visit4;

    @BeforeEach
    void setUp() {
        visitRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();

        doctor1 = Doctor.builder()
                .firstName("Иван")
                .lastName("Петров")
                .middleName("Сергеевич")
                .specialty("Кардиолог")
                .phone("80291234567")
                .email("ivan.petrov@mail.com")
                .information("Опытный кардиолог")
                .build();
        doctor1.setRating(java.math.BigDecimal.valueOf(4.8));

        doctor2 = Doctor.builder()
                .firstName("Мария")
                .lastName("Иванова")
                .middleName("Петровна")
                .specialty("Терапевт")
                .phone("80176543210")
                .email("maria.ivanova@mail.com")
                .information("Врач высшей категории")
                .build();
        doctor2.setRating(java.math.BigDecimal.valueOf(4.5));

        doctorRepository.saveAll(List.of(doctor1, doctor2));

        patient1 = Patient.builder()
                .firstName("Алексей")
                .lastName("Сидоров")
                .middleName("Викторович")
                .dateOfBirth(java.time.LocalDate.of(1980, 5, 15))
                .phone("80251234567")
                .email("alexey.sidorov@mail.com")
                .gender("M")
                .build();

        patient2 = Patient.builder()
                .firstName("Ольга")
                .lastName("Иванова")
                .middleName("Сергеевна")
                .dateOfBirth(java.time.LocalDate.of(1990, 8, 22))
                .phone("80337654321")
                .email("olga.ivanova@mail.com")
                .gender("F")
                .build();

        patient3 = Patient.builder()
                .firstName("Сергей")
                .lastName("Сидоров")
                .middleName("Алексеевич")
                .dateOfBirth(java.time.LocalDate.of(1975, 3, 10))
                .phone("80449876543")
                .email("sergey.sidorov@mail.com")
                .gender("M")
                .build();

        patientRepository.saveAll(List.of(patient1, patient2, patient3));

        LocalDateTime now = LocalDateTime.now();

        visit1 = Visit.builder()
                .dateOfVisit(now.minusDays(1))
                .doctor(doctor1)
                .patient(patient1)
                .status("completed")
                .symptoms("Головная боль, головокружение")
                .diagnosis("Мигрень")
                .prescription("Отдых, анальгин")
                .build();

        visit2 = Visit.builder()
                .dateOfVisit(now.minusHours(2))
                .doctor(doctor1)
                .patient(patient2)
                .status("scheduled")
                .symptoms("Кашель, температура")
                .diagnosis("ОРВИ")
                .prescription("Постельный режим, противовирусные")
                .build();

        visit3 = Visit.builder()
                .dateOfVisit(now.plusDays(1))
                .doctor(doctor2)
                .patient(patient3)
                .status("scheduled")
                .symptoms("Боли в спине")
                .diagnosis("Остеохондроз")
                .prescription("Физиотерапия")
                .build();

        visit4 = Visit.builder()
                .dateOfVisit(now.minusDays(3))
                .doctor(doctor2)
                .patient(patient1)
                .status("completed")
                .symptoms("Высокое давление")
                .diagnosis("Гипертония")
                .prescription("Диета, лекарства от давления")
                .build();

        visitRepository.saveAll(List.of(visit1, visit2, visit3, visit4));
    }

    @Test
    void save_ShouldSaveVisitWithAllFields() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(2);
        Visit newVisit = Visit.builder()
                .dateOfVisit(futureDate)
                .doctor(doctor1)
                .patient(patient2)
                .status("scheduled")
                .symptoms("Боль в горле")
                .diagnosis("Ангина")
                .prescription("Антибиотики, полоскание")
                .build();

        Visit savedVisit = visitRepository.save(newVisit);

        assertThat(savedVisit).isNotNull();
        assertThat(savedVisit.getId()).isNotNull();
        assertThat(savedVisit.getDateOfVisit()).isEqualTo(futureDate);
        assertThat(savedVisit.getDoctor()).isEqualTo(doctor1);
        assertThat(savedVisit.getPatient()).isEqualTo(patient2);
        assertThat(savedVisit.getStatus()).isEqualTo("scheduled");
        assertThat(savedVisit.getSymptoms()).isEqualTo("Боль в горле");
        assertThat(savedVisit.getDiagnosis()).isEqualTo("Ангина");
        assertThat(savedVisit.getPrescription()).isEqualTo("Антибиотики, полоскание");
    }

    @Test
    void save_WithNullOptionalFields_ShouldSaveSuccessfully() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(2);
        Visit newVisit = Visit.builder()
                .dateOfVisit(futureDate)
                .doctor(doctor1)
                .patient(patient2)
                .status("scheduled")
                .symptoms(null)
                .diagnosis("Обследование")
                .prescription(null)
                .build();

        Visit savedVisit = visitRepository.save(newVisit);

        assertThat(savedVisit).isNotNull();
        assertThat(savedVisit.getId()).isNotNull();
        assertThat(savedVisit.getSymptoms()).isNull();
        assertThat(savedVisit.getPrescription()).isNull();
    }


    @Test
    void findByPatientLastNameContainingIgnoreCase_WithNonExistingLastName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Несуществующий";

        List<Visit> result = visitRepository.findByPatientLastNameContainingIgnoreCase(lastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPatientLastNameContainingIgnoreCase_WithPagination_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("dateOfVisit").descending());

        List<Visit> result = visitRepository.findByPatientLastNameContainingIgnoreCase("иванова", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatient().getLastName()).isEqualTo("Иванова");
    }

    @Test
    void findByPatientLastNameAndPatientFirstName_ShouldReturnVisitsWithExactPatientName() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Иванова";
        String firstName = "Ольга";

        List<Visit> result = visitRepository.findByPatientLastNameAndPatientFirstName(lastName, firstName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatient().getLastName()).isEqualTo(lastName);
        assertThat(result.get(0).getPatient().getFirstName()).isEqualTo(firstName);
        assertThat(result.get(0).getId()).isEqualTo(visit2.getId());
    }

    @Test
    void findByPatientLastNameAndPatientFirstName_WithNonExistingName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Несуществующий";
        String firstName = "Пациент";

        List<Visit> result = visitRepository.findByPatientLastNameAndPatientFirstName(lastName, firstName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDoctorId_ShouldReturnVisitsForSpecificDoctor() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID doctorId = doctor1.getId();

        List<Visit> result = visitRepository.findByDoctorId(doctorId, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(visit -> visit.getDoctor().getId())
                .allMatch(id -> id.equals(doctorId));
        assertThat(result).extracting(Visit::getId)
                .containsExactlyInAnyOrder(visit1.getId(), visit2.getId());
    }

    @Test
    void findByDoctorId_WithNonExistingDoctorId_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID nonExistingDoctorId = UUID.randomUUID();

        List<Visit> result = visitRepository.findByDoctorId(nonExistingDoctorId, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPatientId_ShouldReturnVisitsForSpecificPatient() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID patientId = patient1.getId();

        List<Visit> result = visitRepository.findByPatientId(patientId, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(visit -> visit.getPatient().getId())
                .allMatch(id -> id.equals(patientId));
        assertThat(result).extracting(Visit::getId)
                .containsExactlyInAnyOrder(visit1.getId(), visit4.getId());
    }

    @Test
    void findByPatientId_WithNonExistingPatientId_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID nonExistingPatientId = UUID.randomUUID();

        List<Visit> result = visitRepository.findByPatientId(nonExistingPatientId, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByStatus_ShouldReturnVisitsWithSpecificStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        String status = "completed";

        List<Visit> result = visitRepository.findByStatus(status, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Visit::getStatus)
                .allMatch(s -> s.equals(status));
        assertThat(result).extracting(Visit::getId)
                .containsExactlyInAnyOrder(visit1.getId(), visit4.getId());
    }

    @Test
    void findByStatus_WithNonExistingStatus_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String status = "cancelled";

        List<Visit> result = visitRepository.findByStatus(status, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDateOfVisitBetween_ShouldReturnVisitsInDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);

        List<Visit> result = visitRepository.findByDateOfVisitBetween(startDate, endDate, pageable);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Visit::getId)
                .containsExactlyInAnyOrder(visit1.getId(), visit2.getId(), visit3.getId());
    }


    @Test
    void findByDateOfVisitBetween_WithFutureDates_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime startDate = LocalDateTime.now().plusYears(1);
        LocalDateTime endDate = LocalDateTime.now().plusYears(2);

        List<Visit> result = visitRepository.findByDateOfVisitBetween(startDate, endDate, pageable);

        assertThat(result).isEmpty();
    }


    @Test
    void updateById_WithNullOptionalFields_ShouldUpdateSuccessfully() {
        UUID visitId = visit1.getId();
        LocalDateTime newDateOfVisit = LocalDateTime.now().plusDays(5);

        int updatedCount = visitRepository.updateById(
                visitId, newDateOfVisit, doctor1.getId(), patient1.getId(),
                "scheduled", null, "Обследование", null
        );

        assertThat(updatedCount).isEqualTo(1);

        Optional<Visit> updatedVisit = visitRepository.findById(visitId);
        assertThat(updatedVisit).isPresent();
        Visit visit = updatedVisit.get();
        assertThat(visit.getSymptoms()).isNull();
        assertThat(visit.getPrescription()).isNull();
    }

    @Test
    void updateById_WithNonExistingId_ShouldReturnZero() {
        UUID nonExistingId = UUID.randomUUID();

        int updatedCount = visitRepository.updateById(
                nonExistingId, LocalDateTime.now(), doctor1.getId(), patient1.getId(),
                "scheduled", "Симптомы", "Диагноз", "Назначение"
        );

        assertThat(updatedCount).isEqualTo(0);
    }

    @Test
    void deleteById_ShouldRemoveVisit() {
        UUID visitId = visit1.getId();

        visitRepository.deleteById(visitId);

        Optional<Visit> result = visitRepository.findById(visitId);
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WithExistingId_ShouldReturnVisit() {
        Optional<Visit> result = visitRepository.findById(visit1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(visit1.getId());
        assertThat(result.get().getStatus()).isEqualTo("completed");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        UUID nonExistingId = UUID.randomUUID();

        Optional<Visit> result = visitRepository.findById(nonExistingId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllVisits() {
        List<Visit> result = visitRepository.findAll();

        assertThat(result).hasSize(4);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        boolean exists = visitRepository.existsById(visit1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        UUID nonExistingId = UUID.randomUUID();

        boolean exists = visitRepository.existsById(nonExistingId);

        assertThat(exists).isFalse();
    }

    @Test
    void findByDateOfVisitBetween_WithPagination_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("dateOfVisit").ascending());
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);

        List<Visit> result = visitRepository.findByDateOfVisitBetween(startDate, endDate, pageable);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDateOfVisit()).isBeforeOrEqualTo(result.get(1).getDateOfVisit());
    }

    @Test
    void findByStatus_WithScheduledStatus_ShouldReturnScheduledVisits() {
        Pageable pageable = PageRequest.of(0, 10);
        String status = "scheduled";

        List<Visit> result = visitRepository.findByStatus(status, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Visit::getStatus)
                .allMatch(s -> s.equals("scheduled"));
        assertThat(result).extracting(Visit::getId)
                .containsExactlyInAnyOrder(visit2.getId(), visit3.getId());
    }
}