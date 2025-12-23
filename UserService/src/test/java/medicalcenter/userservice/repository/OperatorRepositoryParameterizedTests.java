package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Operator;
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
 * Параметризованные тесты для OperatorRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OperatorRepositoryParameterizedTests {

    @Autowired
    private OperatorRepository repository;

    /**
     * Источник данных для тестирования создания операторов
     */
    private static Stream<Arguments> provideOperatorDataForCreate() {
        return Stream.of(
                Arguments.of("Анна", "Сидорова", "Ивановна", LocalDate.of(1990, 8, 20), "80291234567", "sidorova@callcenter.com"),
                Arguments.of("Ольга", "Кузнецова", "Петровна", LocalDate.of(1993, 3, 15), "80292345678", "kuznetsova@callcenter.com"),
                Arguments.of("Ирина", "Волкова", null, LocalDate.of(1988, 11, 5), "80293456789", "volkova@callcenter.com"),
                Arguments.of("Светлана", "Новикова", "Александровна", LocalDate.of(1995, 6, 12), "80294567890", "novikova@callcenter.com"),
                Arguments.of("Татьяна", "Морозова", "Викторовна", LocalDate.of(1992, 1, 30), "80295678901", "morozova@callcenter.com")
        );
    }

    /**
     * Параметризованный тест для создания оператора (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create operator: {0} {1} {2}")
    @MethodSource("provideOperatorDataForCreate")
    @DisplayName("save() should persist operator with different data sets")
    void testSaveOperator_Parameterized(String firstName, String lastName, String middleName,
                                        LocalDate dateOfBirth, String phone, String email) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email(email)
                .build();

        // Act
        Operator saved = repository.save(operator);

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
     * Параметризованный тест для чтения оператора (READ)
     */
    @ParameterizedTest(name = "[{index}] Read operator by ID: {0} {1}")
    @MethodSource("provideOperatorDataForCreate")
    @DisplayName("findById() should retrieve operator with different data sets")
    void testFindById_Parameterized(String firstName, String lastName, String middleName,
                                    LocalDate dateOfBirth, String phone, String email) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email(email)
                .build();
        Operator saved = repository.save(operator);

        // Act
        Optional<Operator> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo(firstName);
        assertThat(found.get().getLastName()).isEqualTo(lastName);
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    /**
     * Источник данных для тестирования обновления операторов
     */
    private static Stream<Arguments> provideOperatorDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        "OldFirst1", "OldLast1", "OldMiddle1", LocalDate.of(1985, 1, 1), "80291111111", "old1@callcenter.com",
                        "NewFirst1", "NewLast1", "NewMiddle1", LocalDate.of(1995, 1, 1), "80299999991", "new1@callcenter.com"
                ),
                Arguments.of(
                        "OldFirst2", "OldLast2", null, LocalDate.of(1988, 5, 5), "80292222222", "old2@callcenter.com",
                        "NewFirst2", "NewLast2", "NewMiddle2", LocalDate.of(1998, 5, 5), "80299999992", "new2@callcenter.com"
                ),
                Arguments.of(
                        "OldFirst3", "OldLast3", "OldMiddle3", LocalDate.of(1990, 12, 12), "80293333333", "old3@callcenter.com",
                        "NewFirst3", "NewLast3", null, LocalDate.of(2000, 12, 12), "80299999993", "new3@callcenter.com"
                )
        );
    }

    /**
     * Параметризованный тест для обновления оператора (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update operator: {0} -> {6}")
    @MethodSource("provideOperatorDataForUpdate")
    @DisplayName("updateById() should update operator fields with different data sets")
    void testUpdateById_Parameterized(
            String oldFirstName, String oldLastName, String oldMiddleName, LocalDate oldDateOfBirth, String oldPhone, String oldEmail,
            String newFirstName, String newLastName, String newMiddleName, LocalDate newDateOfBirth, String newPhone, String newEmail) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(oldFirstName)
                .lastName(oldLastName)
                .middleName(oldMiddleName)
                .dateOfBirth(oldDateOfBirth)
                .phone(oldPhone)
                .email(oldEmail)
                .build();
        Operator saved = repository.save(operator);
        UUID operatorId = saved.getId();

        // Act
        int updated = repository.updateById(
                operatorId,
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

        Optional<Operator> updatedOperator = repository.findById(operatorId);
        assertThat(updatedOperator).isPresent();
        assertThat(updatedOperator.get().getFirstName()).isEqualTo(newFirstName);
        assertThat(updatedOperator.get().getLastName()).isEqualTo(newLastName);
        assertThat(updatedOperator.get().getMiddleName()).isEqualTo(newMiddleName);
        assertThat(updatedOperator.get().getDateOfBirth()).isEqualTo(newDateOfBirth);
        assertThat(updatedOperator.get().getPhone()).isEqualTo(newPhone);
        assertThat(updatedOperator.get().getEmail()).isEqualTo(newEmail);
    }

    /**
     * Параметризованный тест для удаления оператора (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete operator: {0} {1}")
    @MethodSource("provideOperatorDataForCreate")
    @DisplayName("deleteById() should remove operator with different data sets")
    void testDeleteById_Parameterized(String firstName, String lastName, String middleName,
                                      LocalDate dateOfBirth, String phone, String email) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email(email)
                .build();
        Operator saved = repository.save(operator);
        UUID operatorId = saved.getId();

        assertThat(repository.findById(operatorId)).isPresent();

        // Act
        repository.deleteById(operatorId);

        // Assert
        assertThat(repository.findById(operatorId)).isNotPresent();
    }

    /**
     * Источник данных для тестирования поиска по фамилии
     */
    private static Stream<Arguments> provideOperatorDataForFindByLastName() {
        return Stream.of(
                Arguments.of("Sidorova", "Anna", LocalDate.of(1990, 8, 20), "80291234567"),
                Arguments.of("Kuznetsova", "Olga", LocalDate.of(1993, 3, 15), "80292345678"),
                Arguments.of("Volkova", "Irina", LocalDate.of(1988, 11, 5), "80293456789")
        );
    }

    /**
     * Параметризованный тест для поиска по фамилии
     */
    @ParameterizedTest(name = "[{index}] Find operator by last name: {0}")
    @MethodSource("provideOperatorDataForFindByLastName")
    @DisplayName("findAllByLastName() should find operators by last name")
    void testFindAllByLastName_Parameterized(String lastName, String firstName, LocalDate dateOfBirth, String phone) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email("test@callcenter.com")
                .build();
        repository.save(operator);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Operator> found = repository.findAllByLastName(lastName, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(o -> o.getLastName().equals(lastName) && o.getFirstName().equals(firstName));
    }

    /**
     * Параметризованный тест для поиска по имени и фамилии
     */
    @ParameterizedTest
    @CsvSource({
            "Sidorova, Anna, 1990-08-20, 80291234567",
            "Kuznetsova, Olga, 1993-03-15, 80292345678",
            "Volkova, Irina, 1988-11-05, 80293456789"
    })
    @DisplayName("findAllByLastFirstName() should find operators by first and last name")
    void testFindAllByLastFirstName_Parameterized(String lastName, String firstName, LocalDate dateOfBirth, String phone) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email("test@callcenter.com")
                .build();
        repository.save(operator);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<Operator> found = repository.findAllByLastFirstName(firstName, lastName, pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(o -> o.getLastName().equals(lastName) && o.getFirstName().equals(firstName));
    }

    /**
     * Параметризованный тест для поиска по телефону
     */
    @ParameterizedTest
    @CsvSource({
            "80291234567, Anna, Sidorova",
            "80292345678, Olga, Kuznetsova",
            "80293456789, Irina, Volkova"
    })
    @DisplayName("findByPhone() should find operator by phone number")
    void testFindByPhone_Parameterized(String phone, String firstName, String lastName) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phone(phone)
                .email("test@callcenter.com")
                .build();
        repository.save(operator);

        // Act
        Optional<Operator> found = repository.findByPhone(phone);

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
            "sidorova@callcenter.com, Anna, Sidorova",
            "kuznetsova@callcenter.com, Olga, Kuznetsova",
            "volkova@callcenter.com, Irina, Volkova"
    })
    @DisplayName("findByEmail() should find operator by email")
    void testFindByEmail_Parameterized(String email, String firstName, String lastName) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phone("80290000000")
                .email(email)
                .build();
        repository.save(operator);

        // Act
        Optional<Operator> found = repository.findByEmail(email);

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
            "Sidorova, Anna, Ivanovna, 80291111111, 1990-08-20",
            "Kuznetsova, Olga, Petrovna, 80292222222, 1993-03-15",
            "Volkova, Irina, Alexandrovna, 80293333333, 1988-11-05"
    })
    @DisplayName("findByFullName() should find operator by full name")
    void testFindByFullName_Parameterized(String lastName, String firstName, String middleName, String phone, LocalDate dateOfBirth) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .middleName(middleName)
                .dateOfBirth(dateOfBirth)
                .phone(phone)
                .email("test@callcenter.com")
                .build();
        repository.save(operator);

        // Act
        Optional<Operator> found = repository.findByFullName(lastName, firstName, middleName);

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
            "/avatars/operator1.jpg, Anna, Sidorova",
            "/avatars/operator2.png, Olga, Kuznetsova",
            "/avatars/operator3.gif, Irina, Volkova"
    })
    @DisplayName("updateAvatarPath() should update operator avatar path")
    void testUpdateAvatarPath_Parameterized(String avatarPath, String firstName, String lastName) {
        // Arrange
        Operator operator = Operator.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phone("80290000000")
                .email("test@callcenter.com")
                .build();
        Operator saved = repository.save(operator);

        // Act
        int updated = repository.updateAvatarPath(saved.getId(), avatarPath);

        // Assert
        assertThat(updated).isEqualTo(1);
        Optional<Operator> updatedOperator = repository.findById(saved.getId());
        assertThat(updatedOperator).isPresent();
        assertThat(updatedOperator.get().getAvatarPath()).isEqualTo(avatarPath);
    }
}

