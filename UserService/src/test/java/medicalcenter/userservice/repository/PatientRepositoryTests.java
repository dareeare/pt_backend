package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PatientRepositoryTests {

    @Autowired
    private PatientRepository repository;

    @Test
    @DisplayName("save persists patient and assigns id")
    void testSavePatient() {
        Patient p = Patient.builder()
                .firstName("SaveFirst")
                .lastName("SaveLast")
                .middleName(null)
                .phone("0000")
                .email("save@ex")
                .gender("M")
                .dateOfBirth(LocalDate.of(1999, 9, 9))
                .build();
        Patient saved = repository.save(p);

        assertThat(saved.getId()).isNotNull();
        Optional<Patient> fromDb = repository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getEmail()).isEqualTo("save@ex");
    }


    @Test
    @DisplayName("deleteById removes patient from repository")
    void testDeletePatient() {
        Patient p = Patient.builder()
                .firstName("DelFirst")
                .lastName("DelLast")
                .middleName(null)
                .phone("1111")
                .email("del@ex")
                .gender("F")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
        repository.save(p);
        UUID id = p.getId();
        assertThat(repository.findById(id)).isPresent();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isNotPresent();
    }

    @Test
    @DisplayName("findAllByEmail returns matching patients")
    void testFindAllByEmail() {
        Patient p1 = Patient.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .middleName("S.")
                .phone("123")
                .email("ivan@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1990,1,1))
                .build();
        Patient p2 = Patient.builder()
                .firstName("Petr")
                .lastName("Ivanov")
                .middleName("A.")
                .phone("456")
                .email("petr@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1985,5,5))
                .build();
        Patient p3 = Patient.builder()
                .firstName("Ira")
                .lastName("Sidorova")
                .middleName("B.")
                .phone("789")
                .email("ivan@example.com")
                .gender("F")
                .dateOfBirth(LocalDate.of(1992,2,2))
                .build();

        repository.save(p1);
        repository.save(p2);
        repository.save(p3);

        List<Patient> result = repository.findAllByEmail("ivan@example.com");

        assertThat(result).hasSize(2).extracting("email").containsOnly("ivan@example.com");
    }


    @Test
    @DisplayName("findAllByLastName with pageable returns paged results")
    void testFindAllByLastNameWithPageable() {
        for (int i = 0; i < 5; ++i) {
            repository.save(Patient.builder()
                    .firstName("First" + i)
                    .lastName("Kuznetsov")
                    .middleName(null)
                    .phone("p" + i)
                    .email("k" + i + "@ex")
                    .gender("M")
                    .dateOfBirth(LocalDate.of(1990,1,1))
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 3);
        List<Patient> page1 = repository.findAllByLastName("Kuznetsov", pageable);
        Pageable pageable2 = PageRequest.of(1, 3);
        List<Patient> page2 = repository.findAllByLastName("Kuznetsov", pageable2);

        assertThat(page1).hasSize(3);
        assertThat(page2).hasSize(2);
    }


    @Test
    @DisplayName("findAllByLastFirstName returns matches by first and last name")
    void testFindAllByLastFirstName() {
        repository.save(Patient.builder()
                .firstName("Anna")
                .lastName("Smirnova")
                .middleName(null)
                .phone("11")
                .email("a@ex")
                .gender("F")
                .dateOfBirth(LocalDate.of(1991,3,3))
                .build());
        repository.save(Patient.builder()
                .firstName("Anna")
                .lastName("Smirnova")
                .middleName(null)
                .phone("22")
                .email("b@ex")
                .gender("F")
                .dateOfBirth(LocalDate.of(1992,4,4))
                .build());
        repository.save(Patient.builder()
                .firstName("Anya")
                .lastName("Smirnova")
                .middleName(null)
                .phone("33")
                .email("c@ex")
                .gender("F")
                .dateOfBirth(LocalDate.of(1993,5,5))
                .build());

        Pageable pageable = PageRequest.of(0, 10);
        List<Patient> found = repository.findAllByLastFirstName("Smirnova", "Anna", pageable);

        assertThat(found).hasSize(2).allMatch(p -> "Anna".equals(p.getFirstName()) && "Smirnova".equals(p.getLastName()));
    }


    @Test
    @DisplayName("findByFullName returns exact patient when exists")
    void testFindByFullName() {
        Patient p = Patient.builder()
                .firstName("Olga")
                .lastName("Kovalenko")
                .middleName("Petrovna")
                .phone("100")
                .email("olga@ex")
                .gender("F")
                .dateOfBirth(LocalDate.of(1988,6,6))
                .build();
        repository.save(p);

        Optional<Patient> olga = repository.findByFullName("Kovalenko", "Olga", "Petrovna");
        assertTrue(olga.isPresent());
        Patient patient = olga.get();
        assertEquals("Olga", patient.getFirstName());
        assertEquals("Kovalenko", patient.getLastName());
        assertEquals("Petrovna", patient.getMiddleName());
    }

    @Test
    @DisplayName("findByPhone returns patient if phone matches")
    void testFindByPhone() {
        Patient p = Patient.builder()
                .firstName("Sergey")
                .lastName("Mikhailov")
                .middleName(null)
                .phone("+380501112233")
                .email("ser@ex")
                .gender("M")
                .dateOfBirth(LocalDate.of(1977,7,7))
                .build();
        repository.save(p);

        Optional<Patient> found = repository.findByPhone("+380501112233");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Sergey");
    }

    @Test
    @DisplayName("updateById updates patient fields and returns updated count")
    void testUpdateById() {
        Patient p = Patient.builder()
                .firstName("OldFirst")
                .lastName("OldLast")
                .middleName("OldMiddle")
                .phone("000")
                .email("old@ex")
                .gender("M")
                .dateOfBirth(LocalDate.of(1970,1,1))
                .build();
        repository.save(p);
        UUID id = p.getId();


        int updated = repository.updateById(
                id,
                "NewLast",
                "NewFirst",
                "NewMiddle",
                "999",
                "new@ex",
                LocalDate.of(2000,12,12),
                "F"
        );


        assertThat(updated).isEqualTo(1);


        Optional<Patient> after = repository.findById(id);
        assertThat(after).isPresent();
        Patient ap = after.get();
        assertThat(ap.getFirstName()).isEqualTo("NewFirst");
        assertThat(ap.getLastName()).isEqualTo("NewLast");
        assertThat(ap.getMiddleName()).isEqualTo("NewMiddle");
        assertThat(ap.getPhone()).isEqualTo("999");
        assertThat(ap.getEmail()).isEqualTo("new@ex");
        assertThat(ap.getDateOfBirth()).isEqualTo(LocalDate.of(2000,12,12));
        assertThat(ap.getGender()).isEqualTo("F");
    }
}
