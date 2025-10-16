package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DoctorReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorReviewRepository doctorReviewRepository;

    private Doctor doctor;
    private Patient patient;
    private Visit visit1;
    private Visit visit2;
    private Visit visit3;
    private DoctorReview review1;
    private DoctorReview review2;
    private DoctorReview review3;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .phone("+375251234567")
                .email("john.doe@example.com")
                .specialty("Cardiology")
                .build();

        patient = Patient.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phone("+375449876543")
                .email("jane.smith@example.com")
                .build();

        entityManager.persist(doctor);
        entityManager.persist(patient);
        entityManager.flush();

        visit1 = Visit.builder()
                .doctor(doctor)
                .patient(patient)
                .dateOfVisit(LocalDateTime.now())
                .diagnosis("Common cold")
                .symptoms("Cough, fever")
                .status("completed")
                .build();

        visit2 = Visit.builder()
                .doctor(doctor)
                .patient(patient)
                .dateOfVisit(LocalDateTime.now().minusDays(1))
                .diagnosis("Headache")
                .symptoms("Head pain")
                .status("completed")
                .build();

        visit3 = Visit.builder()
                .doctor(doctor)
                .patient(patient)
                .dateOfVisit(LocalDateTime.now().minusDays(2))
                .diagnosis("Back pain")
                .symptoms("Back discomfort")
                .status("completed")
                .build();

        entityManager.persist(visit1);
        entityManager.persist(visit2);
        entityManager.persist(visit3);
        entityManager.flush();

        review1 = DoctorReview.builder()
                .patient(patient)
                .doctor(doctor)
                .visit(visit1)
                .rating(5)
                .comment("Excellent service")
                .isApproved(true)
                .isEdited(false)
                .build();

        review2 = DoctorReview.builder()
                .patient(patient)
                .doctor(doctor)
                .visit(visit2)
                .rating(4)
                .comment("Good service")
                .isApproved(false)
                .isEdited(false)
                .build();

        review3 = DoctorReview.builder()
                .patient(patient)
                .doctor(doctor)
                .visit(visit3)
                .rating(3)
                .comment("Average service")
                .isApproved(true)
                .isEdited(true)
                .build();

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(review3);
        entityManager.flush();
    }

    @Test
    void findByDoctorId_shouldReturnReviewsForDoctor() {
        Pageable pageable = PageRequest.of(0, 10);

        List<DoctorReview> result = doctorReviewRepository.findByDoctorId(doctor.getId(), pageable);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(DoctorReview::getDoctor)
                .allMatch(d -> d.getId().equals(doctor.getId()));
    }

    @Test
    void findByPatientId_shouldReturnReviewsForPatient() {
        Pageable pageable = PageRequest.of(0, 10);

        List<DoctorReview> result = doctorReviewRepository.findByPatientId(patient.getId(), pageable);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(DoctorReview::getPatient)
                .allMatch(p -> p.getId().equals(patient.getId()));
    }

    @Test
    void findByVisitId_shouldReturnReviewForVisit() {
        Optional<DoctorReview> result = doctorReviewRepository.findByVisitId(visit1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getVisit().getId()).isEqualTo(visit1.getId());
    }

    @Test
    void findApprovedByDoctorId_shouldReturnOnlyApprovedReviews() {
        Pageable pageable = PageRequest.of(0, 10);

        List<DoctorReview> result = doctorReviewRepository.findApprovedByDoctorId(doctor.getId(), pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DoctorReview::getIsApproved)
                .allMatch(Boolean.TRUE::equals);
    }

    @Test
    void findAverageRatingByDoctorId_shouldReturnAverageRating() {
        Double result = doctorReviewRepository.findAverageRatingByDoctorId(doctor.getId());

        assertThat(result).isEqualTo(4.0);
    }

    @Test
    void countApprovedReviewsByDoctorId_shouldReturnApprovedReviewsCount() {
        Long result = doctorReviewRepository.countApprovedReviewsByDoctorId(doctor.getId());

        assertThat(result).isEqualTo(2L);
    }

    @Test
    void updateById_shouldUpdateReview() {
        UUID newPatientId = patient.getId();
        UUID newDoctorId = doctor.getId();
        UUID newVisitId = visit1.getId();
        Integer newRating = 1;
        String newComment = "Updated comment";
        Boolean newIsApproved = true;
        Boolean newIsEdited = true;

        int updatedCount = doctorReviewRepository.updateById(
                review1.getId(),
                newPatientId,
                newDoctorId,
                newVisitId,
                newRating,
                newComment,
                newIsApproved,
                newIsEdited
        );

        assertThat(updatedCount).isEqualTo(1);

        entityManager.clear();
        DoctorReview updatedReview = entityManager.find(DoctorReview.class, review1.getId());

        assertThat(updatedReview.getRating()).isEqualTo(newRating);
        assertThat(updatedReview.getComment()).isEqualTo(newComment);
        assertThat(updatedReview.getIsApproved()).isEqualTo(newIsApproved);
        assertThat(updatedReview.getIsEdited()).isEqualTo(newIsEdited);
    }

    @Test
    void updateById_shouldReturnZeroForNonExistingId() {
        UUID nonExistingId = UUID.randomUUID();

        int updatedCount = doctorReviewRepository.updateById(
                nonExistingId,
                patient.getId(),
                doctor.getId(),
                visit1.getId(),
                5,
                "Test",
                true,
                false
        );

        assertThat(updatedCount).isEqualTo(0);
    }
}