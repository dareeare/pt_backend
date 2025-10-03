package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Сущность представляет пациента медицинского центра.
 * Содержит демографические данные: ФИО, контакты, дата рождения, пол.
 * Связана с историей визитов через коллекцию visits.
 * CHECK-constraint ограничивает значения поля gender ('M', 'F', 'O').
 *
 * Пример: "Пациент: Петров Иван Сергеевич, 01.01.1980, M"
 */

@Data
@NoArgsConstructor
@ToString(exclude = "visits")
@EqualsAndHashCode(exclude = "visits")
@Entity
@Table(name = "Patient")
public class Patient {
    @Id
    @Column(name = "patient_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private char gender;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    @Builder
    public Patient(String firstName, String lastName, String middleName,
                   String phone, String email, char gender, LocalDate dateOfBirth,
                   List<Visit> visits) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.visits = visits;
    }

}
