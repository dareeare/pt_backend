package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Manager;
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
 * Параметризованные тесты для ManagerRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ManagerRepositoryParameterizedTests {

    @Autowired
    private ManagerRepository repository;

    /**
     * Источник данных для тестирования создания менеджеров
     */
    private static Stream<Arguments> provideManagerDataForCreate() {
        return Stream.of(
                Arguments.of("Алексей", "Иванов", "Петрович", LocalDate.of(1985, 5, 15), "80291234567", "ivanov@clinic.com"),
                Arguments.of("Мария", "Петрова", "Алексеевна", LocalDate.of(1990, 8, 20), "80292345678", "petrova@clinic.com"),
                Arguments.of("Дмитрий", "Сидоров", null, LocalDate.of(1983, 12, 10), "80293456789", "sidorov@clinic.com"),
                Arguments.of("Елена", "Смирнова", "Викторовна", LocalDate.of(1988, 3, 25), "80294567890", "smirnova@clinic.com"),
                Arguments.of("Игорь", "Козлов", "Андреевич", LocalDate.of(1992, 7, 5), "80295678901", "kozlov@clinic.com")
        );
    }

    /**
     * Параметризованный тест для создания менеджера (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create manager: {0} {1} {2}")
    @MethodSource("provideManagerDataForCreate")
    @DisplayName("save() should persist manager with different data sets")
    void testSaveManager_Parameterized(String firstName, String lastName, String middleName,
                                       LocalDate dateOfBirth, String phone, String email) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email(email)
                .build();

        // Act
        Manager saved = repository.save(manager);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo(firstName);
        assertThat(saved.getLastName()).isEqualTo(lastName);
        assertThat(saved.getMiddleName()).isEqualTo(middleName);
        assertThat(saved.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(saved.getPhone()).isEqualTo(phone);
        assertThat(saved.getEmail()).isEqualTo(email);
    }

    /**
     * Параметризованный тест для чтения менеджера (READ)
     */
    @ParameterizedTest(name = "[{index}] Read manager by ID: {0} {1}")
    @MethodSource("provideManagerDataForCreate")
    @DisplayName("findById() should retrieve manager with different data sets")
    void testFindById_Parameterized(String firstName, String lastName, String middleName,
                                    LocalDate dateOfBirth, String phone, String email) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email(email)
                .build();
        Manager saved = repository.save(manager);

        // Act
        Optional<Manager> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    /**
     * Источник данных для тестирования обновления менеджеров
     */
    private static Stream<Arguments> provideManagerDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        "OldFirst1", "OldLast1", "OldMiddle1", LocalDate.of(1980, 1, 1), "80291111111", "old1@clinic.com",
                        "NewFirst1", "NewLast1", "NewMiddle1", LocalDate.of(1990, 1, 1), "80299999991", "new1@clinic.com"
                ),
                Arguments.of(
                        "OldFirst2", "OldLast2", null, LocalDate.of(1985, 5, 5), "80292222222", "old2@clinic.com",
                        "NewFirst2", "NewLast2", "NewMiddle2", LocalDate.of(1995, 5, 5), "80299999992", "new2@clinic.com"
                ),
                Arguments.of(
                        "OldFirst3", "OldLast3", "OldMiddle3", LocalDate.of(1975, 12, 12), "80293333333", "old3@clinic.com",
                        "NewFirst3", "NewLast3", null, LocalDate.of(2000, 12, 12), "80299999993", "new3@clinic.com"
                )
        );
    }

    /**
     * Параметризованный тест для обновления менеджера (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update manager: {0} -> {6}")
    @MethodSource("provideManagerDataForUpdate")
    @DisplayName("updateById() should update manager fields with different data sets")
    void testUpdateById_Parameterized(
            String oldFirstName, String oldLastName, String oldMiddleName, LocalDate oldDateOfBirth, String oldPhone, String oldEmail,
            String newFirstName, String newLastName, String newMiddleName, LocalDate newDateOfBirth, String newPhone, String newEmail) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(oldFirstName)
                .lastName(oldLastName)
                .middleName(oldMiddleName)
                .dateOfBirth(oldDateOfBirth)
                .phone(oldPhone)
                .email(oldEmail)
                .build();
        Manager saved = repository.save(manager);
        UUID managerId = saved.getId();

        // Act
        int updated = repository.updateById(
                managerId,
                newLastName,
                newFirstName,
                newMiddleName,
                newDateOfBirth,
                newPhone,
                newEmail,
                "/avatars/updated.png"
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<Manager> updatedManager = repository.findById(managerId);
        assertThat(updatedManager).isPresent();
        assertThat(updatedManager.get().getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedManager.get().getLastName()).isEqualTo(newLastName);
        assertThat(updatedManager.get().getMiddleName()).isEqualTo(newMiddleName);
        assertThat(updatedManager.get().getDateOfBirth()).isEqualTo(newDateOfBirth);
        assertThat(updatedManager.get().getPhone()).isEqualTo(newPhone);
        assertThat(updatedManager.get().getEmail()).isEqualTo(newEmail);
    }

    /**
     * Параметризованный тест для удаления менеджера (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete manager: {0} {1}")
    @MethodSource("provideManagerDataForCreate")
    @DisplayName("deleteById() should remove manager with different data sets")
    void testDeleteById_Parameterized(String firstName, String lastName, String middleName,
                                      LocalDate dateOfBirth, String phone, String email) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email(email)
                .build();
        Manager saved = repository.save(manager);
        UUID managerId = saved.getId();

        assertThat(repository.findById(managerId)).isPresent();

        // Act
        repository.deleteById(managerId);

        // Assert
        assertThat(repository.findById(managerId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по фамилии
     */
    @ParameterizedTest
    @CsvSource({
            "Ivanov, Alexey, 1985-05-15, 80291234567",
            "Petrova, Maria, 1990-08-20, 80292345678",
            "Sidorov, Dmitry, 1983-12-10, 80293456789"
    })
    @DisplayName("findAllByLastName() should find managers by last name")
    void testFindAllByLastName_Parameterized(String lastName, String firstName, LocalDate dateOfBirth, String phone) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email("test@clinic.com")
                .build();
        repository.save(manager);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Manager> found = repository.findAllByLastName(lastName, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(m -> m.getLastName().equals(lastName) && m.getFirstName().equals(firstName));
    }

    /**
     * Параметризованный тест для поиска по имени и фамилии
     */
    @ParameterizedTest
    @CsvSource({
            "Ivanov, Alexey, 1985-05-15, 80291234567",
            "Petrova, Maria, 1990-08-20, 80292345678",
            "Sidorov, Dmitry, 1983-12-10, 80293456789"
    })
    @DisplayName("findAllByLastFirstName() should find managers by first and last name")
    void testFindAllByLastFirstName_Parameterized(String lastName, String firstName, LocalDate dateOfBirth, String phone) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email("test@clinic.com")
                .build();
        repository.save(manager);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Manager> found = repository.findAllByLastFirstName(firstName, lastName, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(m -> m.getLastName().equals(lastName) && m.getFirstName().equals(firstName));
    }

    /**
     * Параметризованный тест для поиска по телефону
     */
    @ParameterizedTest
    @CsvSource({
            "80291234567, Alexey, Ivanov",
            "80292345678, Maria, Petrova",
            "80293456789, Dmitry, Sidorov"
    })
    @DisplayName("findByPhone() should find manager by phone number")
    void testFindByPhone_Parameterized(String phone, String firstName, String lastName) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1985, 1, 1))
                .phone(phone)
                .email("test@clinic.com")
                .build();
        repository.save(manager);

        // Act
        Optional<Manager> found = repository.findByPhone(phone);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getPhone()).isEqualTo(phone);
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
    }

    /**
     * Параметризованный тест для поиска по email
     */
    @ParameterizedTest
    @CsvSource({
            "ivanov@clinic.com, Alexey, Ivanov",
            "petrova@clinic.com, Maria, Petrova",
            "sidorov@clinic.com, Dmitry, Sidorov"
    })
    @DisplayName("findByEmail() should find manager by email")
    void testFindByEmail_Parameterized(String email, String firstName, String lastName) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1985, 1, 1))
                .phone("80290000000")
                .email(email)
                .build();
        repository.save(manager);

        // Act
        Optional<Manager> found = repository.findByEmail(email);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
    }

    /**
     * Параметризованный тест для поиска по полному имени
     */
    @ParameterizedTest
    @CsvSource({
            "Ivanov, Alexey, Petrovich, 80291111111, 1985-05-15",
            "Petrova, Maria, Alexeevna, 80292222222, 1990-08-20",
            "Sidorov, Dmitry, Ivanovich, 80293333333, 1983-12-10"
    })
    @DisplayName("findByFullName() should find manager by full name")
    void testFindByFullName_Parameterized(String lastName, String firstName, String middleName, String phone, LocalDate dateOfBirth) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email("test@clinic.com")
                .build();
        repository.save(manager);

        // Act
        Optional<Manager> found = repository.findByFullName(lastName, firstName, middleName);

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
            "/avatars/manager1.jpg, Alexey, Ivanov",
            "/avatars/manager2.png, Maria, Petrova",
            "/avatars/manager3.gif, Dmitry, Sidorov"
    })
    @DisplayName("updateAvatarPath() should update manager avatar path")
    void testUpdateAvatarPath_Parameterized(String avatarPath, String firstName, String lastName) {
        // Arrange
        Manager manager = Manager.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1985, 1, 1))
                .phone("80290000000")
                .email("test@clinic.com")
                .build();
        Manager saved = repository.save(manager);

        // Act
        int updated = repository.updateAvatarPath(saved.getId(), avatarPath);

        // Assert
        assertThat(updated).isEqualTo(1);
        Optional<Manager> updatedManager = repository.findById(saved.getId());
        assertThat(updatedManager).isPresent();
        assertThat(updatedManager.get().getAvatarPath()).isEqualTo(avatarPath);
    }
}

