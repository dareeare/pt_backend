package medicalcenter.userservice.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Doctor")
public class Doctor {
    @Id
    @Column(name = "doctor_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID doctorId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "rating")
    private Float rating;

    @OneToMany
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