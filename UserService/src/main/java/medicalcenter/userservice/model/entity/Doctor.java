package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString(exclude = "visits")
@EqualsAndHashCode(exclude = "visits")
@Entity
@Table(name = "Doctor")
public class Doctor {
    @Id
    @Column(name = "doctor_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    @NotEmpty(message = "First name should not be empty")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotEmpty(message = "Last name should not be empty")
    private String lastName;

    @Column(name = "middle_name")
    @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
    private String middleName;

    @Column(name = "phone")
    @NotEmpty(message = "Phone should not be empty")
    @Pattern(regexp = "^80(29|33|44|17|25)\\d{7}$")
    private String phone;

    @Column(name = "email")
    @Email
    private String email;

    @Column(name = "specialty", nullable = false)
    @NotBlank(message = "Specialty should not be empty")
    private String specialty;

    @Column(name = "rating")
    private Float rating;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    @Builder
    public Doctor(String firstName, String lastName, String middleName,
                  String specialty, String phone, String email, Float rating,
                  List<Visit> visits) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
        this.visits = visits;
        this.rating = rating; //возможно надо будет удалить
    }

}