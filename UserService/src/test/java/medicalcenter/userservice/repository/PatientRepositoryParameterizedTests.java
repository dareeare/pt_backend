package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Patient;
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
 * Параметризованные тесты для PatientRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PatientRepositoryParameterizedTests {

    @Autowired
    private PatientRepository repository;

    /**
     * Источник данных для тестирования создания пациентов
     */
    private static Stream<Arguments> providePatientDataForCreate() {
        return Stream.of(
                Arguments.of("Иван", "Петров", "Сергеевич", "80291234567", "ivan@example.com", "M", LocalDate.of(1990, 1, 1)),
                Arguments.of("Мария", "Иванова", "Алексеевна", "80292345678", "maria@example.com", "F", LocalDate.of(1985, 5, 15)),
                Arguments.of("Петр", "Сидоров", null, "80293456789", "petr@example.com", "M", LocalDate.of(1978, 12, 30)),
                Arguments.of("Анна", "Козлова", "Дмитриевна", "80294567890", "anna@example.com", "F", LocalDate.of(1995, 7, 20)),
                Arguments.of("Олег", "Смирнов", "Викторович", "80295678901", "oleg@example.com", "O", LocalDate.of(1982, 3, 8))
        );
    }

    /**
     * Параметризованный тест для создания пациента (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create patient: {0} {1} {2}")
    @MethodSource("providePatientDataForCreate")
    @DisplayName("save() should persist patient with different data sets")
    void testSavePatient_Parameterized(String firstName, String lastName, String middleName,
                                       String phone, String email, String gender, LocalDate dateOfBirth) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .phone(phone)
                .email(email)
                .gender(gender)
                .dateOfBirth(dateOfBirth)
                .build();

        // Act
        Patient saved = repository.save(patient);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo(firstName);
        assertThat(saved.getLastName()).isEqualTo(lastName);
        assertThat(saved.getMiddleName()).isEqualTo(middleName);
        assertThat(saved.getPhone()).isEqualTo(phone);
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getGender()).isEqualTo(gender);
        assertThat(saved.getDateOfBirth()).isEqualTo(dateOfBirth);
    }

    /**
     * Параметризованный тест для чтения пациента (READ)
     */
    @ParameterizedTest(name = "[{index}] Read patient by ID: {0} {1}")
    @MethodSource("providePatientDataForCreate")
    @DisplayName("findById() should retrieve patient with different data sets")
    void testFindById_Parameterized(String firstName, String lastName, String middleName,
                                    String phone, String email, String gender, LocalDate dateOfBirth) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .phone(phone)
                .email(email)
                .gender(gender)
                .dateOfBirth(dateOfBirth)
                .build();
        Patient saved = repository.save(patient);

        // Act
        Optional<Patient> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    /**
     * Источник данных для тестирования обновления пациентов
     */
    private static Stream<Arguments> providePatientDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        "OldFirst1", "OldLast1", "OldMiddle1", "80291111111", "old1@ex.com", "M", LocalDate.of(1980, 1, 1),
                        "NewFirst1", "NewLast1", "NewMiddle1", "80299999991", "new1@ex.com", "F", LocalDate.of(1990, 1, 1)
                ),
                Arguments.of(
                        "OldFirst2", "OldLast2", null, "80292222222", "old2@ex.com", "F", LocalDate.of(1985, 5, 5),
                        "NewFirst2", "NewLast2", "NewMiddle2", "80299999992", "new2@ex.com", "M", LocalDate.of(1995, 5, 5)
                ),
                Arguments.of(
                        "OldFirst3", "OldLast3", "OldMiddle3", "80293333333", "old3@ex.com", "O", LocalDate.of(1975, 12, 12),
                        "NewFirst3", "NewLast3", null, "80299999993", "new3@ex.com", "F", LocalDate.of(2000, 12, 12)
                )
        );
    }

    /**
     * Параметризованный тест для обновления пациента (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update patient: {0} -> {7}")
    @MethodSource("providePatientDataForUpdate")
    @DisplayName("updateById() should update patient fields with different data sets")
    void testUpdateById_Parameterized(
            String oldFirstName, String oldLastName, String oldMiddleName, String oldPhone, String oldEmail, String oldGender, LocalDate oldDateOfBirth,
            String newFirstName, String newLastName, String newMiddleName, String newPhone, String newEmail, String newGender, LocalDate newDateOfBirth) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(oldFirstName)
                .lastName(oldLastName)
                .middleName(oldMiddleName)
                .phone(oldPhone)
                .email(oldEmail)
                .gender(oldGender)
                .dateOfBirth(oldDateOfBirth)
                .build();
        Patient saved = repository.save(patient);
        UUID patientId = saved.getId();

        // Act
        int updated = repository.updateById(
                patientId,
                newLastName,
                newFirstName,
                newMiddleName,
                newPhone,
                newEmail,
                newDateOfBirth,
                newGender,
                "/avatars/updated.png"
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<Patient> updatedPatient = repository.findById(patientId);
        assertThat(updatedPatient).isPresent();
        assertThat(updatedPatient.get().getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedPatient.get().getLastName()).isEqualTo(newLastName);
        assertThat(updatedPatient.get().getMiddleName()).isEqualTo(newMiddleName);
        assertThat(updatedPatient.get().getPhone()).isEqualTo(newPhone);
        assertThat(updatedPatient.get().getEmail()).isEqualTo(newEmail);
        assertThat(updatedPatient.get().getGender()).isEqualTo(newGender);
        assertThat(updatedPatient.get().getDateOfBirth()).isEqualTo(newDateOfBirth);
    }

    /**
     * Параметризованный тест для удаления пациента (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete patient: {0} {1}")
    @MethodSource("providePatientDataForCreate")
    @DisplayName("deleteById() should remove patient with different data sets")
    void testDeleteById_Parameterized(String firstName, String lastName, String middleName,
                                      String phone, String email, String gender, LocalDate dateOfBirth) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .phone(phone)
                .email(email)
                .gender(gender)
                .dateOfBirth(dateOfBirth)
                .build();
        Patient saved = repository.save(patient);
        UUID patientId = saved.getId();

        assertThat(repository.findById(patientId)).isPresent();

        // Act
        repository.deleteById(patientId);

        // Assert
        assertThat(repository.findById(patientId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по email
     */
    @ParameterizedTest
    @CsvSource({
            "ivan@example.com, Ivan, Petrov, M, 1990-01-01",
            "maria@example.com, Maria, Ivanova, F, 1985-05-15",
            "petr@example.com, Petr, Sidorov, M, 1978-12-30"
    })
    @DisplayName("findAllByEmail() should find patients by email")
    void testFindAllByEmail_Parameterized(String email, String firstName, String lastName, String gender, LocalDate dateOfBirth) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone("80290000000")
                .email(email)
                .gender(gender)
                .dateOfBirth(dateOfBirth)
                .build();
        repository.save(patient);

        // Act
        List<Patient> found = repository.findAllByEmail(email);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(p -> p.getEmail().equals(email) && p.getFirstName().equals(firstName));
    }

    /**
     * Параметризованный тест для поиска по фамилии с пагинацией
     */
    @ParameterizedTest
    @CsvSource({
            "Kuznetsov, 5, 3, 2",
            "Smirnov, 10, 4, 6",
            "Petrov, 7, 5, 2"
    })
    @DisplayName("findAllByLastName() should return paginated results")
    void testFindAllByLastName_Parameterized(String lastName, int totalRecords, int pageSize, int expectedOnSecondPage) {
        // Arrange
        for (int i = 0; i < totalRecords; i++) {
            Patient patient = Patient.builder()
                    .firstName("First" + i)
                    .lastName(lastName)
                    .phone("phone" + i)
                    .email("email" + i + "@example.com")
                    .gender("M")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .build();
            repository.save(patient);
        }

        // Act
        Pageable pageable1 = PageRequest.of(0, pageSize);
        List<Patient> page1 = repository.findAllByLastName(lastName, pageable1);

        Pageable pageable2 = PageRequest.of(1, pageSize);
        List<Patient> page2 = repository.findAllByLastName(lastName, pageable2);

        // Assert
        assertThat(page1).hasSize(pageSize);
        assertThat(page2).hasSize(expectedOnSecondPage);
    }

    /**
     * Параметризованный тест для поиска по телефону
     */
    @ParameterizedTest
    @CsvSource({
            "80291234567, Sergey, Mikhailov",
            "80292345678, Anna, Kovalenko",
            "80293456789, Dmitry, Volkov"
    })
    @DisplayName("findByPhone() should find patient by phone number")
    void testFindByPhone_Parameterized(String phone, String firstName, String lastName) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .email("test@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1985, 1, 1))
                .build();
        repository.save(patient);

        // Act
        Optional<Patient> found = repository.findByPhone(phone);

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
            "Kovalenko, Olga, Petrovna, 80291111111, 1988-06-06",
            "Ivanov, Dmitry, Alexandrovich, 80292222222, 1992-03-15",
            "Sidorova, Elena, Ivanovna, 80293333333, 1987-09-25"
    })
    @DisplayName("findByFullName() should find patient by full name")
    void testFindByFullName_Parameterized(String lastName, String firstName, String middleName, String phone, LocalDate dateOfBirth) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .phone(phone)
                .email("test@example.com")
                .gender("F")
                .dateOfBirth(dateOfBirth)
                .build();
        repository.save(patient);

        // Act
        Optional<Patient> found = repository.findByFullName(lastName, firstName, middleName);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
        assertThat(found.get().getMiddleName()).isEqualTo(middleName);
    }

    /**
     * Параметризованный тест для обновления аватара
     */
    @ParameterizedTest
    @CsvSource({
            "/avatars/patient1.jpg, Ivan, Petrov",
            "/avatars/patient2.png, Maria, Sidorova",
            "/avatars/patient3.gif, Petr, Ivanov"
    })
    @DisplayName("updateAvatarPath() should update avatar path")
    void testUpdateAvatarPath_Parameterized(String avatarPath, String firstName, String lastName) {
        // Arrange
        Patient patient = Patient.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone("80290000000")
                .email("test@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        Patient saved = repository.save(patient);

        // Act
        int updated = repository.updateAvatarPath(saved.getId(), avatarPath);

        // Assert
        assertThat(updated).isEqualTo(1);
        Optional<Patient> updatedPatient = repository.findById(saved.getId());
        assertThat(updatedPatient).isPresent();
        assertThat(updatedPatient.get().getAvatarPath()).isEqualTo(avatarPath);
    }
}

