package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для DoctorRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DoctorRepositoryParameterizedTests {

    @Autowired
    private DoctorRepository repository;

    /**
     * Источник данных для тестирования создания врачей
     */
    private static Stream<Arguments> provideDoctorDataForCreate() {
        return Stream.of(
                Arguments.of("Иван", "Иванов", "Иванович", "Кардиолог", "80291234567", "ivanov@clinic.com", "15 лет опыта"),
                Arguments.of("Мария", "Петрова", "Алексеевна", "Терапевт", "80292345678", "petrova@clinic.com", "10 лет опыта"),
                Arguments.of("Петр", "Сидоров", null, "Хирург", "80293456789", "sidorov@clinic.com", "20 лет опыта"),
                Arguments.of("Анна", "Смирнова", "Дмитриевна", "Педиатр", "80294567890", "smirnova@clinic.com", "8 лет опыта"),
                Arguments.of("Олег", "Козлов", "Викторович", "Невролог", "80295678901", "kozlov@clinic.com", "12 лет опыта")
        );
    }

    /**
     * Параметризованный тест для создания врача (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create doctor: {0} {1}, специальность: {3}")
    @MethodSource("provideDoctorDataForCreate")
    @DisplayName("save() should persist doctor with different data sets")
    void testSaveDoctor_Parameterized(String firstName, String lastName, String middleName,
                                      String specialty, String phone, String email, String information) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .specialty(specialty)
                .phone(phone)
                .email(email)
                .information(information)
                .build();

        // Act
        Doctor saved = repository.save(doctor);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo(firstName);
        assertThat(saved.getLastName()).isEqualTo(lastName);
        assertThat(saved.getMiddleName()).isEqualTo(middleName);
        assertThat(saved.getSpecialty()).isEqualTo(specialty);
        assertThat(saved.getPhone()).isEqualTo(phone);
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getInformation()).isEqualTo(information);
        assertThat(saved.getRating()).isEqualTo(BigDecimal.ZERO);
    }

    /**
     * Параметризованный тест для чтения врача (READ)
     */
    @ParameterizedTest(name = "[{index}] Read doctor by ID: {0} {1}")
    @MethodSource("provideDoctorDataForCreate")
    @DisplayName("findById() should retrieve doctor with different data sets")
    void testFindById_Parameterized(String firstName, String lastName, String middleName,
                                    String specialty, String phone, String email, String information) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .specialty(specialty)
                .phone(phone)
                .email(email)
                .information(information)
                .build();
        Doctor saved = repository.save(doctor);

        // Act
        Optional<Doctor> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
        assertThat(found.get().getSpecialty()).isEqualTo(specialty);
    }

    /**
     * Источник данных для тестирования обновления врачей
     */
    private static Stream<Arguments> provideDoctorDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        "OldFirst1", "OldLast1", "OldMiddle1", "OldSpecialty1", "80291111111", "old1@clinic.com", "Old info 1", BigDecimal.ZERO,
                        "NewFirst1", "NewLast1", "NewMiddle1", "NewSpecialty1", "80299999991", "new1@clinic.com", "New info 1", new BigDecimal("4.5")
                ),
                Arguments.of(
                        "OldFirst2", "OldLast2", null, "OldSpecialty2", "80292222222", "old2@clinic.com", "Old info 2", BigDecimal.ZERO,
                        "NewFirst2", "NewLast2", "NewMiddle2", "NewSpecialty2", "80299999992", "new2@clinic.com", "New info 2", new BigDecimal("3.8")
                ),
                Arguments.of(
                        "OldFirst3", "OldLast3", "OldMiddle3", "OldSpecialty3", "80293333333", "old3@clinic.com", "Old info 3", BigDecimal.ONE,
                        "NewFirst3", "NewLast3", null, "NewSpecialty3", "80299999993", "new3@clinic.com", "New info 3", new BigDecimal("4.9")
                )
        );
    }

    /**
     * Параметризованный тест для обновления врача (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update doctor: {0} -> {8}")
    @MethodSource("provideDoctorDataForUpdate")
    @DisplayName("updateById() should update doctor fields with different data sets")
    void testUpdateById_Parameterized(
            String oldFirstName, String oldLastName, String oldMiddleName, String oldSpecialty, String oldPhone, String oldEmail, String oldInformation, BigDecimal oldRating,
            String newFirstName, String newLastName, String newMiddleName, String newSpecialty, String newPhone, String newEmail, String newInformation, BigDecimal newRating) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(oldFirstName)
                .lastName(oldLastName)
                .middleName(oldMiddleName)
                .specialty(oldSpecialty)
                .phone(oldPhone)
                .email(oldEmail)
                .information(oldInformation)
                .build();
        repository.save(doctor);
        doctor.setRating(oldRating);
        repository.save(doctor);
        UUID doctorId = doctor.getId();

        // Act
        int updated = repository.updateById(
                doctorId,
                newLastName,
                newFirstName,
                newMiddleName,
                newSpecialty,
                newPhone,
                newEmail,
                newInformation,
                newRating,
                "/avatars/updated.png"
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<Doctor> updatedDoctor = repository.findById(doctorId);
        assertThat(updatedDoctor).isPresent();
        assertThat(updatedDoctor.get().getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedDoctor.get().getLastName()).isEqualTo(newLastName);
        assertThat(updatedDoctor.get().getMiddleName()).isEqualTo(newMiddleName);
        assertThat(updatedDoctor.get().getSpecialty()).isEqualTo(newSpecialty);
        assertThat(updatedDoctor.get().getPhone()).isEqualTo(newPhone);
        assertThat(updatedDoctor.get().getEmail()).isEqualTo(newEmail);
        assertThat(updatedDoctor.get().getInformation()).isEqualTo(newInformation);
        assertThat(updatedDoctor.get().getRating()).isEqualTo(newRating);
    }

    /**
     * Параметризованный тест для удаления врача (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete doctor: {0} {1}")
    @MethodSource("provideDoctorDataForCreate")
    @DisplayName("deleteById() should remove doctor with different data sets")
    void testDeleteById_Parameterized(String firstName, String lastName, String middleName,
                                      String specialty, String phone, String email, String information) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .specialty(specialty)
                .phone(phone)
                .email(email)
                .information(information)
                .build();
        Doctor saved = repository.save(doctor);
        UUID doctorId = saved.getId();

        assertThat(repository.findById(doctorId)).isPresent();

        // Act
        repository.deleteById(doctorId);

        // Assert
        assertThat(repository.findById(doctorId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по фамилии
     */
    @ParameterizedTest
    @CsvSource({
            "Ivanov, Ivan, Кардиолог, 80291234567",
            "Petrov, Petr, Терапевт, 80292345678",
            "Sidorov, Sergey, Хирург, 80293456789"
    })
    @DisplayName("findAllByLastName() should find doctors by last name")
    void testFindAllByLastName_Parameterized(String lastName, String firstName, String specialty, String phone) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialty(specialty)
                .phone(phone)
                .email("test@clinic.com")
                .build();
        repository.save(doctor);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Doctor> found = repository.findAllByLastName(lastName, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(d -> d.getLastName().equals(lastName) && d.getFirstName().equals(firstName));
    }

    /**
     * Параметризованный тест для поиска по специальности
     */
    @ParameterizedTest
    @CsvSource({
            "Кардиолог, 5, 3",
            "Терапевт, 10, 4",
            "Хирург, 7, 5"
    })
    @DisplayName("findBySpecialty() should return doctors by specialty with pagination")
    void testFindBySpecialty_Parameterized(String specialty, int totalRecords, int pageSize) {
        // Arrange
        for (int i = 0; i < totalRecords; i++) {
            Doctor doctor = Doctor.builder()
                    .firstName("First" + i)
                    .lastName("Last" + i)
                    .specialty(specialty)
                    .phone("phone" + i)
                    .email("email" + i + "@clinic.com")
                    .build();
            repository.save(doctor);
        }

        // Act
        Pageable pageable = PageRequest.of(0, pageSize);
        List<Doctor> found = repository.findBySpecialty(specialty, pageable);

        // Assert
        assertThat(found).hasSize(pageSize);
        assertThat(found).allMatch(d -> d.getSpecialty().equals(specialty));
    }

    /**
     * Параметризованный тест для поиска по телефону
     */
    @ParameterizedTest
    @CsvSource({
            "80291234567, Ivan, Ivanov, Кардиолог",
            "80292345678, Maria, Petrova, Терапевт",
            "80293456789, Petr, Sidorov, Хирург"
    })
    @DisplayName("findByPhone() should find doctor by phone number")
    void testFindByPhone_Parameterized(String phone, String firstName, String lastName, String specialty) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialty(specialty)
                .phone(phone)
                .email("test@clinic.com")
                .build();
        repository.save(doctor);

        // Act
        Optional<Doctor> found = repository.findByPhone(phone);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getPhone()).isEqualTo(phone);
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
    }

    /**
     * Параметризованный тест для поиска по полному имени
     */
    @ParameterizedTest
    @CsvSource({
            "Ivanov, Ivan, Ivanovich, Кардиолог, 80291111111",
            "Petrova, Maria, Alexeevna, Терапевт, 80292222222",
            "Sidorov, Petr, Sergeevich, Хирург, 80293333333"
    })
    @DisplayName("findByFullName() should find doctor by full name")
    void testFindByFullName_Parameterized(String lastName, String firstName, String middleName, String specialty, String phone) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .specialty(specialty)
                .phone(phone)
                .email("test@clinic.com")
                .build();
        repository.save(doctor);

        // Act
        Optional<Doctor> found = repository.findByFullName(lastName, firstName, middleName);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
        assertThat(found.get().getMiddleName()).isEqualTo(middleName);
    }

    /**
     * Параметризованный тест для поиска по рейтингу
     */
    @ParameterizedTest
    @CsvSource({
            "4.0, Ivan, Ivanov, Кардиолог, 4.5",
            "3.5, Maria, Petrova, Терапевт, 3.8",
            "4.5, Petr, Sidorov, Хирург, 4.9"
    })
    @DisplayName("findByRatingGreaterThanEqual() should find doctors by minimum rating")
    void testFindByRatingGreaterThanEqual_Parameterized(Double minRating, String firstName, String lastName, String specialty, String rating) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialty(specialty)
                .phone("80290000000")
                .email("test@clinic.com")
                .build();
        repository.save(doctor);
        doctor.setRating(new BigDecimal(rating));
        repository.save(doctor);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Doctor> found = repository.findByRatingGreaterThanEqual(minRating, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(d -> 
                d.getFirstName().equals(firstName) && 
                d.getRating().compareTo(new BigDecimal(minRating.toString())) >= 0
        );
    }

    /**
     * Параметризованный тест для обновления аватара
     */
    @ParameterizedTest
    @CsvSource({
            "/avatars/doctor1.jpg, Ivan, Ivanov, Кардиолог",
            "/avatars/doctor2.png, Maria, Petrova, Терапевт",
            "/avatars/doctor3.gif, Petr, Sidorov, Хирург"
    })
    @DisplayName("updateAvatarPath() should update doctor avatar path")
    void testUpdateAvatarPath_Parameterized(String avatarPath, String firstName, String lastName, String specialty) {
        // Arrange
        Doctor doctor = Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialty(specialty)
                .phone("80290000000")
                .email("test@clinic.com")
                .build();
        Doctor saved = repository.save(doctor);

        // Act
        int updated = repository.updateAvatarPath(saved.getId(), avatarPath);

        // Assert
        assertThat(updated).isEqualTo(1);
        Optional<Doctor> updatedDoctor = repository.findById(saved.getId());
        assertThat(updatedDoctor).isPresent();
        assertThat(updatedDoctor.get().getAvatarPath()).isEqualTo(avatarPath);
    }
}

