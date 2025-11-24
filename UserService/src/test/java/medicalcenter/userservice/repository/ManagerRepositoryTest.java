package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Manager;
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
class ManagerRepositoryTest {

    @Autowired
    private ManagerRepository managerRepository;

    private Manager manager1;
    private Manager manager2;
    private Manager manager3;

    @BeforeEach
    void setUp() {
        managerRepository.deleteAll();

        manager1 = Manager.builder()
                .firstName("Анна")
                .lastName("Иванова")
                .middleName("Петровна")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .phone("80291234567")
                .email("anna.ivanova@medicalcenter.com")
                .build();

        manager2 = Manager.builder()
                .firstName("Сергей")
                .lastName("Петров")
                .middleName("Александрович")
                .dateOfBirth(LocalDate.of(1990, 8, 22))
                .phone("80176543210")
                .email("sergey.petrov@medicalcenter.com")
                .build();

        manager3 = Manager.builder()
                .firstName("Мария")
                .lastName("Иванова")
                .middleName("Сергеевна")
                .dateOfBirth(LocalDate.of(1988, 3, 10))
                .phone("80339876543")
                .email("maria.ivanova@medicalcenter.com")
                .build();

        managerRepository.saveAll(List.of(manager1, manager2, manager3));
    }

    @Test
    void save_ShouldSaveManagerWithAllFields() {
        Manager newManager = Manager.builder()
                .firstName("Дмитрий")
                .lastName("Сидоров")
                .middleName("Владимирович")
                .dateOfBirth(LocalDate.of(1992, 12, 5))
                .phone("80441122334")
                .email("dmitry.sidorov@medicalcenter.com")
                .build();

        Manager savedManager = managerRepository.save(newManager);

        assertThat(savedManager).isNotNull();
        assertThat(savedManager.getId()).isNotNull();
        assertThat(savedManager.getFirstName()).isEqualTo("Дмитрий");
        assertThat(savedManager.getLastName()).isEqualTo("Сидоров");
        assertThat(savedManager.getMiddleName()).isEqualTo("Владимирович");
        assertThat(savedManager.getDateOfBirth()).isEqualTo(LocalDate.of(1992, 12, 5));
        assertThat(savedManager.getPhone()).isEqualTo("80441122334");
        assertThat(savedManager.getEmail()).isEqualTo("dmitry.sidorov@medicalcenter.com");
    }

    @Test
    void save_WithoutRequiredFields_ShouldThrowException() {
        Manager invalidManager = new Manager();
        invalidManager.setPhone("80255667788");
        invalidManager.setEmail("test@mail.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            managerRepository.saveAndFlush(invalidManager);
        });
    }

    @Test
    void findAllByLastName_ShouldReturnManagersWithGivenLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Иванова";

        List<Manager> result = managerRepository.findAllByLastName(lastName, pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Manager::getLastName)
                .allMatch(lastName::equals);
        assertThat(result).extracting(Manager::getId)
                .containsExactlyInAnyOrder(manager1.getId(), manager3.getId());
    }

    @Test
    void findAllByLastName_WithPagination_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("firstName"));
        String lastName = "Иванова";

        List<Manager> result = managerRepository.findAllByLastName(lastName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Анна");
    }

    @Test
    void findAllByLastName_WithNonExistingLastName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String lastName = "Несуществующий";

        List<Manager> result = managerRepository.findAllByLastName(lastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByLastFirstName_ShouldReturnManagersWithGivenFirstAndLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "Анна";
        String lastName = "Иванова";

        List<Manager> result = managerRepository.findAllByLastFirstName(firstName, lastName, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo(firstName);
        assertThat(result.get(0).getLastName()).isEqualTo(lastName);
        assertThat(result.get(0).getId()).isEqualTo(manager1.getId());
    }

    @Test
    void findAllByLastFirstName_WithNonExistingName_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        String firstName = "Несуществующий";
        String lastName = "Менеджер";

        List<Manager> result = managerRepository.findAllByLastFirstName(firstName, lastName, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    void findByFullName_ShouldReturnManagerWithExactFullName() {
        String lastName = "Иванова";
        String firstName = "Анна";
        String middleName = "Петровна";

        Optional<Manager> result = managerRepository.findByFullName(lastName, firstName, middleName);

        assertThat(result).isPresent();
        Manager manager = result.get();
        assertThat(manager.getFirstName()).isEqualTo(firstName);
        assertThat(manager.getLastName()).isEqualTo(lastName);
        assertThat(manager.getMiddleName()).isEqualTo(middleName);
        assertThat(manager.getId()).isEqualTo(manager1.getId());
    }

    @Test
    void findByFullName_WithNonExistingFullName_ShouldReturnEmpty() {
        String lastName = "Несуществующий";
        String firstName = "Менеджер";
        String middleName = "Тестовый";

        Optional<Manager> result = managerRepository.findByFullName(lastName, firstName, middleName);

        assertThat(result).isEmpty();
    }

    @Test
    void findByPhone_ShouldReturnManagerWithGivenPhone() {
        String phone = "80291234567";

        Optional<Manager> result = managerRepository.findByPhone(phone);

        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo(phone);
        assertThat(result.get().getId()).isEqualTo(manager1.getId());
    }

    @Test
    void findByPhone_WithNonExistingPhone_ShouldReturnEmpty() {
        String phone = "80250000000";

        Optional<Manager> result = managerRepository.findByPhone(phone);

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_ShouldReturnManagerWithGivenEmail() {
        String email = "anna.ivanova@medicalcenter.com";

        Optional<Manager> result = managerRepository.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getId()).isEqualTo(manager1.getId());
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        String email = "nonexisting@medicalcenter.com";

        Optional<Manager> result = managerRepository.findByEmail(email);

        assertThat(result).isEmpty();
    }

    @Test
    void updateById_ShouldUpdateAllManagerFields() {
        UUID managerId = manager1.getId();
        String newLastName = "Смирнова";
        String newFirstName = "Анна-Мария";
        String newMiddleName = "Владимировна";
        LocalDate newDateOfBirth = LocalDate.of(1986, 6, 20);
        String newPhone = "80170000000";
        String newEmail = "anna.smirnova@medicalcenter.com";
        String newAvatarPath = "/avatars/managers/smirnova.png";

        int updatedCount = managerRepository.updateById(
                managerId, newLastName, newFirstName, newMiddleName,
                newDateOfBirth, newPhone, newEmail, newAvatarPath
        );

        assertThat(updatedCount).isEqualTo(1);

        Optional<Manager> updatedManager = managerRepository.findById(managerId);
        assertThat(updatedManager).isPresent();
        Manager manager = updatedManager.get();
        assertThat(manager.getLastName()).isEqualTo(newLastName);
        assertThat(manager.getFirstName()).isEqualTo(newFirstName);
        assertThat(manager.getMiddleName()).isEqualTo(newMiddleName);
        assertThat(manager.getDateOfBirth()).isEqualTo(newDateOfBirth);
        assertThat(manager.getPhone()).isEqualTo(newPhone);
        assertThat(manager.getEmail()).isEqualTo(newEmail);
        assertThat(manager.getAvatarPath()).isEqualTo(newAvatarPath);
    }

    @Test
    void updateById_WithNonExistingId_ShouldReturnZero() {
        UUID nonExistingId = UUID.randomUUID();

        int updatedCount = managerRepository.updateById(
                nonExistingId, "НоваяФамилия", "НовоеИмя", "НовоеОтчество",
                LocalDate.of(1990, 1, 1), "80330000000", "new@medicalcenter.com", "/avatars/none.png"
        );

        assertThat(updatedCount).isEqualTo(0);
    }

    @Test
    void updateById_WithNullFields_ShouldUpdateWithNullValues() {
        UUID managerId = manager1.getId();

        int updatedCount = managerRepository.updateById(
                managerId, "НоваяФамилия", "НовоеИмя", null,
                LocalDate.of(1990, 1, 1), "80440000000", null, null
        );

        assertThat(updatedCount).isEqualTo(1);

        Optional<Manager> updatedManager = managerRepository.findById(managerId);
        assertThat(updatedManager).isPresent();
        Manager manager = updatedManager.get();
        assertThat(manager.getMiddleName()).isNull();
        assertThat(manager.getEmail()).isNull();
    }

    @Test
    void deleteById_ShouldRemoveManager() {
        UUID managerId = manager1.getId();

        managerRepository.deleteById(managerId);

        Optional<Manager> result = managerRepository.findById(managerId);
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WithExistingId_ShouldReturnManager() {
        Optional<Manager> result = managerRepository.findById(manager1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(manager1.getId());
        assertThat(result.get().getFirstName()).isEqualTo("Анна");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        UUID nonExistingId = UUID.randomUUID();

        Optional<Manager> result = managerRepository.findById(nonExistingId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllManagers() {
        List<Manager> result = managerRepository.findAll();

        assertThat(result).hasSize(3);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        boolean exists = managerRepository.existsById(manager1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        UUID nonExistingId = UUID.randomUUID();

        boolean exists = managerRepository.existsById(nonExistingId);

        assertThat(exists).isFalse();
    }
}