package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@ToString(exclude = {"visits", "services", "schedules", "reviews"})
@EqualsAndHashCode(exclude = {"visits", "services", "schedules", "reviews"})
@Entity
@Table(name = "Doctor")
public class Doctor {
    @Id
    @Column(name = "doctor_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "specialty", nullable = false)
    private String specialty;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "information")
    private String information;

    @Column(name = "rating")
    private BigDecimal rating;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorReview> reviews;

    @Builder
    public Doctor(String firstName, String lastName, String middleName,
                  String specialty, String phone, String email,
                  String information) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
        this.information = information;
        this.rating = BigDecimal.ZERO;
        List<Schedule> schedules = new ArrayList<>();
        List<Service> services = new ArrayList<>();
        List<DoctorReview> reviews = new ArrayList<>();
    }
}