package medicalcenter.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
    @NotEmpty(message = "First name should not be empty")
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotEmpty(message = "Last name should not be empty")
    @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
    private String lastName;

    @Column(name = "middle_name")
    @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
    private String middleName;

    @Column(name = "phone")
    @NotEmpty(message = "Phone should not be empty")
    private String phone;

    @Column(name = "email")
    @Size(max = 100)
    private String email;

    @Column(name = "date_of_birth")
    @NotNull(message = "Date of birth should not be empty")
    @Past(message = "Date of birth should be in the past")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    @NotEmpty(message = "Gender should not be empty")
    @Pattern(regexp = "^[MFO]$", message = "Gender must be one of 'M', 'F', or 'O'")
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
