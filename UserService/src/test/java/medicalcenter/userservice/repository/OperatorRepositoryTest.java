package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
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
class OperatorRepositoryTest {

    @Autowired
    private OperatorRepository operatorRepository;

    private Operator operator1;
    private Operator operator2;
    private Operator operator3;

    @BeforeEach
    void setUp() {
        operatorRepository.deleteAll();

        operator1 = Operator.builder()
                .firstName("Екатерина")
                .lastName("Соколова")
                .middleName("Андреевна")
                .dateOfBirth(LocalDate.of(1993, 4, 12))
                .phone("80291234567")
                .email("ekaterina.sokolova@medicalcenter.com")
                .build();

        operator2 = Operator.builder()
                .firstName("Александр")
                .lastName("Попов")
                .middleName("Игоревич")
                .dateOfBirth(LocalDate.of(1995, 9, 5))
                .phone("80176543210")
                .email("alexander.popov@medicalcenter.com")
                .build();

        operator3 = Operator.builder()
                .firstName("Ольга")
                .lastName("Соколова")
                .middleName("Викторовна")
                .dateOfBirth(LocalDate.of(1991, 11, 20))
                .phone("80339876543")
                .email("olga.sokolova@medicalcenter.com")
                .build();

        operatorRepository.saveAll(List.of(operator1, operator2, operator3));
    }

    @Test
    void save_ShouldSaveOperatorWithAllFields() {
        Operator newOperator = Operator.builder()
                .firstName("Иван")
                .lastName("Новиков")
                .middleName("Дмитриевич")
                .dateOfBirth(LocalDate.of(1994, 7, 8))
                .phone("80441122334")
                .email("ivan.novikov@medicalcenter.com")
                .build();

        Operator savedOperator = operatorRepository.save(newOperator);

        assertThat(savedOperator).isNotNull();
        assertThat(savedOperator.getId()).isNotNull();
        assertThat(savedOperator.getFirstName()).isEqualTo("Иван");
        assertThat(savedOperator.getLastName()).isEqualTo("Новиков");
        assertThat(savedOperator.getMiddleName()).isEqualTo("Дмитриевич");
        assertThat(savedOperator.getDateOfBirth()).isEqualTo(LocalDate.of(1994, 7, 8));
        assertThat(savedOperator.getPhone()).isEqualTo("80441122334");
        assertThat(savedOperator.getEmail()).isEqualTo("ivan.novikov@medicalcenter.com");
    }

    @Test
    void save_WithoutRequiredFields_ShouldThrowException() {
        Operator invalidOperator = new Operator();
        invalidOperator.setPhone("80255667788");
        invalidOperator.setEmail("test@mail.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            operatorRepository.saveAndFlush(invalidOperator);
        });
    }

    @Test
    void findAllByLastName_ShouldReturnOperatorsWithGivenLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Соколова";

        List<Operator> result = operatorRepository.findAllByLastName(lastName, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Operator::getLastName)
                .allMatch(lastName::equals);
        assertThat(result).extracting(Operator::getId)
                .containsExactlyInAnyOrder(operator1.getId(), operator3.getId());
    }

    @Test
    void findAllByLastName_WithPagination_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("firstName"));
        String lastName = "Соколова";

        List<Operator> result = operatorRepository.findAllByLastName(lastName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Екатерина");
    }

    @Test
    void findAllByLastName_WithNonExistingLastName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Несуществующий";

        List<Operator> result = operatorRepository.findAllByLastName(lastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByLastFirstName_ShouldReturnOperatorsWithGivenFirstAndLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "Екатерина";
        String lastName = "Соколова";

        List<Operator> result = operatorRepository.findAllByLastFirstName(firstName, lastName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo(firstName);
        assertThat(result.get(0).getLastName()).isEqualTo(lastName);
        assertThat(result.get(0).getId()).isEqualTo(operator1.getId());
    }

    @Test
    void findAllByLastFirstName_WithNonExistingName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "Несуществующий";
        String lastName = "Оператор";

        List<Operator> result = operatorRepository.findAllByLastFirstName(firstName, lastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByFullName_ShouldReturnOperatorWithExactFullName() {
        String lastName = "Соколова";
        String firstName = "Екатерина";
        String middleName = "Андреевна";

        Optional<Operator> result = operatorRepository.findByFullName(lastName, firstName, middleName);

        assertThat(result).isPresent();
        Operator operator = result.get();
        assertThat(operator.getFirstName()).isEqualTo(firstName);
        assertThat(operator.getLastName()).isEqualTo(lastName);
        assertThat(operator.getMiddleName()).isEqualTo(middleName);
        assertThat(operator.getId()).isEqualTo(operator1.getId());
    }

    @Test
    void findByFullName_WithNonExistingFullName_ShouldReturnEmpty() {
        String lastName = "Несуществующий";
        String firstName = "Оператор";
        String middleName = "Тестовый";

        Optional<Operator> result = operatorRepository.findByFullName(lastName, firstName, middleName);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPhone_ShouldReturnOperatorWithGivenPhone() {
        String phone = "80291234567";

        Optional<Operator> result = operatorRepository.findByPhone(phone);

        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo(phone);
        assertThat(result.get().getId()).isEqualTo(operator1.getId());
    }

    @Test
    void findByPhone_WithNonExistingPhone_ShouldReturnEmpty() {
        String phone = "80250000000";

        Optional<Operator> result = operatorRepository.findByPhone(phone);

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_ShouldReturnOperatorWithGivenEmail() {
        String email = "ekaterina.sokolova@medicalcenter.com";

        Optional<Operator> result = operatorRepository.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getId()).isEqualTo(operator1.getId());
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        String email = "nonexisting@medicalcenter.com";

        Optional<Operator> result = operatorRepository.findByEmail(email);

        assertThat(result).isEmpty();
    }

    @Test
    void updateById_ShouldUpdateAllOperatorFields() {
        UUID operatorId = operator1.getId();
        String newLastName = "Орлова";
        String newFirstName = "Екатерина-Мария";
        String newMiddleName = "Сергеевна";
        LocalDate newDateOfBirth = LocalDate.of(1994, 5, 18);
        String newPhone = "80170000000";
        String newEmail = "ekaterina.orlova@medicalcenter.com";
        String newAvatarPath = "/avatars/operators/orlova.png";

        int updatedCount = operatorRepository.updateById(
                operatorId, newLastName, newFirstName, newMiddleName,
                newDateOfBirth, newPhone, newEmail, newAvatarPath
        );

        assertThat(updatedCount).isEqualTo(1);

        Optional<Operator> updatedOperator = operatorRepository.findById(operatorId);
        assertThat(updatedOperator).isPresent();
        Operator operator = updatedOperator.get();
        assertThat(operator.getLastName()).isEqualTo(newLastName);
        assertThat(operator.getFirstName()).isEqualTo(newFirstName);
        assertThat(operator.getMiddleName()).isEqualTo(newMiddleName);
        assertThat(operator.getDateOfBirth()).isEqualTo(newDateOfBirth);
        assertThat(operator.getPhone()).isEqualTo(newPhone);
        assertThat(operator.getEmail()).isEqualTo(newEmail);
        assertThat(operator.getAvatarPath()).isEqualTo(newAvatarPath);
    }

    @Test
    void updateById_WithNonExistingId_ShouldReturnZero() {
        UUID nonExistingId = UUID.randomUUID();

        int updatedCount = operatorRepository.updateById(
                nonExistingId, "НоваяФамилия", "НовоеИмя", "НовоеОтчество",
                LocalDate.of(1990, 1, 1), "80330000000", "new@medicalcenter.com", "/avatars/none.png"
        );

        assertThat(updatedCount).isEqualTo(0);
    }

    @Test
    void updateById_WithNullFields_ShouldUpdateWithNullValues() {
        UUID operatorId = operator1.getId();

        int updatedCount = operatorRepository.updateById(
                operatorId, "НоваяФамилия", "НовоеИмя", null,
                LocalDate.of(1990, 1, 1), "80440000000", null, null
        );

        assertThat(updatedCount).isEqualTo(1);

        Optional<Operator> updatedOperator = operatorRepository.findById(operatorId);
        assertThat(updatedOperator).isPresent();
        Operator operator = updatedOperator.get();
        assertThat(operator.getMiddleName()).isNull();
        assertThat(operator.getEmail()).isNull();
    }

    @Test
    void deleteById_ShouldRemoveOperator() {
        UUID operatorId = operator1.getId();

        operatorRepository.deleteById(operatorId);

        Optional<Operator> result = operatorRepository.findById(operatorId);
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WithExistingId_ShouldReturnOperator() {
        Optional<Operator> result = operatorRepository.findById(operator1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(operator1.getId());
        assertThat(result.get().getFirstName()).isEqualTo("Екатерина");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        UUID nonExistingId = UUID.randomUUID();

        Optional<Operator> result = operatorRepository.findById(nonExistingId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllOperators() {
        List<Operator> result = operatorRepository.findAll();

        assertThat(result).hasSize(3);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        boolean exists = operatorRepository.existsById(operator1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        UUID nonExistingId = UUID.randomUUID();

        boolean exists = operatorRepository.existsById(nonExistingId);

        assertThat(exists).isFalse();
    }
}