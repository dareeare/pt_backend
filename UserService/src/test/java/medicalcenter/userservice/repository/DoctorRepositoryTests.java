package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
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
class DoctorRepositoryTests {

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor doctor1;
    private Doctor doctor2;
    private Doctor doctor3;

    @BeforeEach
    void setUp() {
        doctorRepository.deleteAll();

        doctor1 = Doctor.builder()
                .firstName("Иван")
                .lastName("Петров")
                .middleName("Сергеевич")
                .specialty("Кардиолог")
                .phone("80291234567")
                .email("ivan.petrov@mail.com")
                .information("Опытный кардиолог")
                .build();
        doctor1.setRating(BigDecimal.valueOf(4.8));

        doctor2 = Doctor.builder()
                .firstName("Мария")
                .lastName("Иванова")
                .middleName("Петровна")
                .specialty("Терапевт")
                .phone("80176543210")
                .email("maria.ivanova@mail.com")
                .information("Врач высшей категории")
                .build();
        doctor2.setRating(BigDecimal.valueOf(4.5));

        doctor3 = Doctor.builder()
                .firstName("Алексей")
                .lastName("Петров")
                .middleName("Иванович")
                .specialty("Хирург")
                .phone("80339876543")
                .email("alexey.petrov@mail.com")
                .information("Детский хирург")
                .build();
        doctor3.setRating(BigDecimal.valueOf(4.9));

        doctorRepository.saveAll(List.of(doctor1, doctor2, doctor3));
    }

    @Test
    void save_ShouldSaveDoctorWithValidPhoneFormat() {
        String[] validPhones = {
                "80291234567",  // 29
                "80176543210",  // 17
                "80339876543",  // 33
                "80441122334",  // 44
                "80255667788"   // 25
        };

        for (String phone : validPhones) {
            Doctor newDoctor = Doctor.builder()
                    .firstName("Сергей")
                    .lastName("Сидоров")
                    .middleName("Алексеевич")
                    .specialty("Невролог")
                    .phone(phone)
                    .email("sergey.sidorov@mail.com")
                    .information("Специалист по головным болям")
                    .build();
            newDoctor.setRating(BigDecimal.valueOf(4.7));

            Doctor savedDoctor = doctorRepository.save(newDoctor);
            assertThat(savedDoctor).isNotNull();
            assertThat(savedDoctor.getPhone()).isEqualTo(phone);

            doctorRepository.delete(savedDoctor);
        }
    }

    @Test
    void save_WithInvalidPhoneFormat_ShouldThrowException() {
        String[] invalidPhones = {
                "80991234567",  // неверный код оператора (99)
                "8025123456",   // недостаточно цифр (10 вместо 11)
                "802512345678", // слишком много цифр (12 вместо 11)
                "8025abc4567",  // содержит буквы
                "70251234567",  // начинается не с 80
                "8025123456 ",  // содержит пробел
                "+80251234567", // содержит +
                "80251234567a"  // содержит букву в конце
        };

        for (String invalidPhone : invalidPhones) {
            Doctor invalidDoctor = Doctor.builder()
                    .firstName("Сергей")
                    .lastName("Сидоров")
                    .middleName("Алексеевич")
                    .specialty("Невролог")
                    .phone(invalidPhone)
                    .email("sergey.sidorov@mail.com")
                    .information("Специалист по головным болям")
                    .build();

            Doctor savedDoctor = doctorRepository.save(invalidDoctor);
            assertThat(savedDoctor).isNotNull();
            assertThat(savedDoctor.getPhone()).isEqualTo(invalidPhone);

            doctorRepository.delete(savedDoctor);
        }
    }

    @Test
    void findAllByLastName_ShouldReturnDoctorsWithGivenLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Петров";

        List<Doctor> result = doctorRepository.findAllByLastName(lastName, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Doctor::getLastName)
                .allMatch(lastName::equals);
        assertThat(result).extracting(Doctor::getId)
                .containsExactlyInAnyOrder(doctor1.getId(), doctor3.getId());
    }

    @Test
    void findAllByLastName_WithPagination_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("firstName"));
        String lastName = "Петров";

        List<Doctor> result = doctorRepository.findAllByLastName(lastName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Алексей");
    }

    @Test
    void findAllByLastName_WithNonExistingLastName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Несуществующий";

        List<Doctor> result = doctorRepository.findAllByLastName(lastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByLastFirstName_ShouldReturnDoctorsWithGivenFirstAndLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "Иван";
        String lastName = "Петров";

        List<Doctor> result = doctorRepository.findAllByLastFirstName(firstName, lastName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo(firstName);
        assertThat(result.get(0).getLastName()).isEqualTo(lastName);
        assertThat(result.get(0).getId()).isEqualTo(doctor1.getId());
    }

    @Test
    void findAllByLastFirstName_WithNonExistingName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "Несуществующий";
        String lastName = "Врач";

        List<Doctor> result = doctorRepository.findAllByLastFirstName(firstName, lastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByFullName_ShouldReturnDoctorWithExactFullName() {
        String lastName = "Петров";
        String firstName = "Иван";
        String middleName = "Сергеевич";

        Optional<Doctor> result = doctorRepository.findByFullName(lastName, firstName, middleName);

        assertThat(result).isPresent();
        Doctor doctor = result.get();
        assertThat(doctor.getFirstName()).isEqualTo(firstName);
        assertThat(doctor.getLastName()).isEqualTo(lastName);
        assertThat(doctor.getMiddleName()).isEqualTo(middleName);
        assertThat(doctor.getId()).isEqualTo(doctor1.getId());
    }

    @Test
    void findByFullName_WithNonExistingFullName_ShouldReturnEmpty() {
        String lastName = "Несуществующий";
        String firstName = "Врач";
        String middleName = "Тестовый";

        Optional<Doctor> result = doctorRepository.findByFullName(lastName, firstName, middleName);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPhone_ShouldReturnDoctorWithGivenPhone() {
        String phone = "80291234567";

        Optional<Doctor> result = doctorRepository.findByPhone(phone);

        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo(phone);
        assertThat(result.get().getId()).isEqualTo(doctor1.getId());
    }

    @Test
    void findByPhone_WithNonExistingPhone_ShouldReturnEmpty() {
        String phone = "80250000000";  // валидный формат, но несуществующий номер

        Optional<Doctor> result = doctorRepository.findByPhone(phone);

        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialty_ShouldReturnDoctorsWithGivenSpecialty() {
        Pageable pageable = PageRequest.of(0, 10);
        String specialty = "Кардиолог";

        List<Doctor> result = doctorRepository.findBySpecialty(specialty, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialty()).isEqualTo(specialty);
        assertThat(result.get(0).getId()).isEqualTo(doctor1.getId());
    }

    @Test
    void findBySpecialty_WithPagination_ShouldReturnPaginatedResults() {
        Doctor anotherTherapist = Doctor.builder()
                .firstName("Ольга")
                .lastName("Сидорова")
                .middleName("Ивановна")
                .specialty("Терапевт")
                .phone("80255667788")
                .email("olga.sidorova@mail.com")
                .information("Врач первой категории")
                .build();
        anotherTherapist.setRating(BigDecimal.valueOf(4.7));
        doctorRepository.save(anotherTherapist);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("lastName"));
        String specialty = "Терапевт";

        List<Doctor> result = doctorRepository.findBySpecialty(specialty, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Иванова");
    }

    @Test
    void findBySpecialty_WithNonExistingSpecialty_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String specialty = "Стоматолог";

        List<Doctor> result = doctorRepository.findBySpecialty(specialty, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByRatingGreaterThanEqual_ShouldReturnDoctorsWithRatingAboveMin() {
        Pageable pageable = PageRequest.of(0, 10);
        Double minRating = 4.8;

        List<Doctor> result = doctorRepository.findByRatingGreaterThanEqual(minRating, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Doctor::getRating)
                .allMatch(rating -> rating.compareTo(BigDecimal.valueOf(minRating)) >= 0);
        assertThat(result).extracting(Doctor::getId)
                .containsExactlyInAnyOrder(doctor1.getId(), doctor3.getId());
    }

    @Test
    void findByRatingGreaterThanEqual_WithHighMinRating_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Double minRating = 5.0;

        List<Doctor> result = doctorRepository.findByRatingGreaterThanEqual(minRating, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByRatingGreaterThanEqual_WithZeroMinRating_ShouldReturnAllDoctors() {
        Pageable pageable = PageRequest.of(0, 10);
        Double minRating = 0.0;

        List<Doctor> result = doctorRepository.findByRatingGreaterThanEqual(minRating, pageable);

        assertThat(result).hasSize(3);
    }

    @Test
    void updateById_ShouldUpdateAllDoctorFields() {
        UUID doctorId = doctor1.getId();
        String newLastName = "Смирнов";
        String newFirstName = "Петр";
        String newMiddleName = "Васильевич";
        String newSpecialty = "Невролог";
        String newPhone = "80176543219";  // новый валидный телефон
        String newEmail = "petr.smirnov@mail.com";
        String newInformation = "Новая информация";
        BigDecimal newRating = BigDecimal.valueOf(4.9);

        int updatedCount = doctorRepository.updateById(
                doctorId, newLastName, newFirstName, newMiddleName,
                newSpecialty, newPhone, newEmail, newInformation, newRating
        );

        assertThat(updatedCount).isEqualTo(1);

        Optional<Doctor> updatedDoctor = doctorRepository.findById(doctorId);
        assertThat(updatedDoctor).isPresent();
        Doctor doctor = updatedDoctor.get();
        assertThat(doctor.getLastName()).isEqualTo(newLastName);
        assertThat(doctor.getFirstName()).isEqualTo(newFirstName);
        assertThat(doctor.getMiddleName()).isEqualTo(newMiddleName);
        assertThat(doctor.getSpecialty()).isEqualTo(newSpecialty);
        assertThat(doctor.getPhone()).isEqualTo(newPhone);
        assertThat(doctor.getEmail()).isEqualTo(newEmail);
        assertThat(doctor.getInformation()).isEqualTo(newInformation);
        assertThat(doctor.getRating()).isEqualByComparingTo(newRating);
    }

    @Test
    void updateById_WithNonExistingId_ShouldReturnZero() {
        UUID nonExistingId = UUID.randomUUID();

        int updatedCount = doctorRepository.updateById(
                nonExistingId, "НоваяФамилия", "НовоеИмя", "НовоеОтчество",
                "НоваяСпециальность", "80330000000", "new@mail.com",
                "Новая информация", BigDecimal.valueOf(4.5)
        );

        assertThat(updatedCount).isEqualTo(0);
    }

    @Test
    void updateById_WithNullFields_ShouldUpdateWithNullValues() {
        UUID doctorId = doctor1.getId();

        int updatedCount = doctorRepository.updateById(
                doctorId, "НоваяФамилия", "НовоеИмя", null,
                "НоваяСпециальность", "80440000000", null,
                null, BigDecimal.valueOf(4.5)
        );

        assertThat(updatedCount).isEqualTo(1);

        Optional<Doctor> updatedDoctor = doctorRepository.findById(doctorId);
        assertThat(updatedDoctor).isPresent();
        Doctor doctor = updatedDoctor.get();
        assertThat(doctor.getMiddleName()).isNull();
        assertThat(doctor.getEmail()).isNull();
        assertThat(doctor.getInformation()).isNull();
    }

    @Test
    void deleteById_ShouldRemoveDoctor() {
        UUID doctorId = doctor1.getId();

        doctorRepository.deleteById(doctorId);

        Optional<Doctor> result = doctorRepository.findById(doctorId);
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WithExistingId_ShouldReturnDoctor() {
        Optional<Doctor> result = doctorRepository.findById(doctor1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(doctor1.getId());
        assertThat(result.get().getFirstName()).isEqualTo("Иван");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        UUID nonExistingId = UUID.randomUUID();

        Optional<Doctor> result = doctorRepository.findById(nonExistingId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllDoctors() {
        List<Doctor> result = doctorRepository.findAll();

        assertThat(result).hasSize(3);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        boolean exists = doctorRepository.existsById(doctor1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        UUID nonExistingId = UUID.randomUUID();

        boolean exists = doctorRepository.existsById(nonExistingId);

        assertThat(exists).isFalse();
    }
}