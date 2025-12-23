package medicalcenter.userservice.service;

import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.DoctorReview;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.DoctorReviewRepository;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.repository.VisitRepository;
import medicalcenter.userservice.service.impl.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@Transactional
class DoctorServiceTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorReviewRepository doctorReviewRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Doctor testDoctor;
    private Patient testPatient;
    private Visit testVisit1;
    private Visit testVisit2;
    private Visit testVisit3;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        doctorReviewRepository.deleteAll();
        visitRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();

        // Создание тестового врача
        testDoctor = Doctor.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .specialty("Кардиолог")
                .phone("80291234567")
                .email("ivanov@clinic.com")
                .build();
        testDoctor = doctorRepository.save(testDoctor);

        // Создание тестового пациента
        testPatient = Patient.builder()
                .firstName("Петр")
                .lastName("Сидоров")
                .phone("80293456789")
                .email("sidorov@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
        testPatient = patientRepository.save(testPatient);

        // Создание тестовых визитов
        testVisit1 = Visit.builder()
                .dateOfVisit(LocalDateTime.now().minusDays(5))
                .status("completed")
                .symptoms("Головная боль")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        testVisit1 = visitRepository.save(testVisit1);

        testVisit2 = Visit.builder()
                .dateOfVisit(LocalDateTime.now().minusDays(3))
                .status("completed")
                .symptoms("Высокое давление")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        testVisit2 = visitRepository.save(testVisit2);

        testVisit3 = Visit.builder()
                .dateOfVisit(LocalDateTime.now().minusDays(1))
                .status("completed")
                .symptoms("Боль в груди")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        testVisit3 = visitRepository.save(testVisit3);
    }

    @Test
    void getRating_WhenDoctorHasNoRating_ShouldReturnZero() {
        // Given
        UUID doctorId = testDoctor.getId();

        // When
        BigDecimal rating = doctorService.getRating(doctorId);

        // Then
        assertThat(rating).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getRating_WhenDoctorHasRating_ShouldReturnCurrentRating() {
        // Given
        UUID doctorId = testDoctor.getId();
        testDoctor.setRating(new BigDecimal("4.5"));
        testDoctor = doctorRepository.save(testDoctor);

        // When
        BigDecimal rating = doctorService.getRating(doctorId);

        // Then
        assertThat(rating).isEqualByComparingTo(new BigDecimal("4.5"));
    }

    @Test
    void getRating_WhenDoctorNotFound_ShouldThrowNotFoundException() {
        // Given
        UUID nonExistentDoctorId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> doctorService.getRating(nonExistentDoctorId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void recalculateRating_WhenNoApprovedReviews_ShouldSetRatingToZero() {
        // Given
        UUID doctorId = testDoctor.getId();

        // Создаем неодобренные отзывы
        DoctorReview review1 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(5)
                .comment("Отличный врач")
                .isApproved(false)
                .build();
        doctorReviewRepository.save(review1);

        DoctorReview review2 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit2)
                .rating(4)
                .comment("Хороший врач")
                .isApproved(false)
                .build();
        doctorReviewRepository.save(review2);

        // When
        BigDecimal newRating = doctorService.recalculateRating(doctorId);

        // Then
        assertThat(newRating).isEqualByComparingTo(BigDecimal.ZERO);
        
        // Проверяем, что рейтинг обновлен в базе
        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void recalculateRating_WhenHasApprovedReviews_ShouldCalculateAverageRating() {
        // Given
        UUID doctorId = testDoctor.getId();

        // Создаем одобренные отзывы: 5, 4, 3 = среднее 4.0
        DoctorReview review1 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(5)
                .comment("Отличный врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review1);

        DoctorReview review2 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit2)
                .rating(4)
                .comment("Хороший врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review2);

        DoctorReview review3 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit3)
                .rating(3)
                .comment("Средний врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review3);

        // When
        BigDecimal newRating = doctorService.recalculateRating(doctorId);

        // Then
        assertThat(newRating).isEqualByComparingTo(new BigDecimal("4.00"));
        
        // Проверяем, что рейтинг обновлен в базе
        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("4.00"));
    }

    @Test
    void recalculateRating_WhenHasMixedApprovedAndUnapprovedReviews_ShouldCalculateOnlyApproved() {
        // Given
        UUID doctorId = testDoctor.getId();

        // Одобренные отзывы: 5, 4 = среднее 4.5
        DoctorReview approvedReview1 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(5)
                .comment("Отличный врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(approvedReview1);

        DoctorReview approvedReview2 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit2)
                .rating(4)
                .comment("Хороший врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(approvedReview2);

        // Неодобренные отзывы (не должны учитываться)
        DoctorReview unapprovedReview = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit3)
                .rating(1)
                .comment("Плохой врач")
                .isApproved(false)
                .build();
        doctorReviewRepository.save(unapprovedReview);

        // When
        BigDecimal newRating = doctorService.recalculateRating(doctorId);

        // Then
        assertThat(newRating).isEqualByComparingTo(new BigDecimal("4.50"));
        
        // Проверяем, что рейтинг обновлен в базе
        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("4.50"));
    }

    @Test
    void recalculateRating_WhenRatingShouldBeRounded_ShouldRoundToTwoDecimals() {
        // Given
        UUID doctorId = testDoctor.getId();

        // Одобренные отзывы: 5, 4, 5 = среднее 4.666... должно округлиться до 4.67
        DoctorReview review1 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(5)
                .comment("Отличный врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review1);

        DoctorReview review2 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit2)
                .rating(4)
                .comment("Хороший врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review2);

        DoctorReview review3 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit3)
                .rating(5)
                .comment("Отличный врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review3);

        // When
        BigDecimal newRating = doctorService.recalculateRating(doctorId);

        // Then
        assertThat(newRating).isEqualByComparingTo(new BigDecimal("4.67"));
        
        // Проверяем, что рейтинг обновлен в базе
        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("4.67"));
    }

    @Test
    void recalculateRating_WhenDoctorNotFound_ShouldThrowNotFoundException() {
        // Given
        UUID nonExistentDoctorId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> doctorService.recalculateRating(nonExistentDoctorId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void recalculateRating_WhenSingleApprovedReview_ShouldSetRatingToThatValue() {
        // Given
        UUID doctorId = testDoctor.getId();

        // Один одобренный отзыв с рейтингом 5
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(5)
                .comment("Отличный врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review);

        // When
        BigDecimal newRating = doctorService.recalculateRating(doctorId);

        // Then
        assertThat(newRating).isEqualByComparingTo(new BigDecimal("5.00"));
        
        // Проверяем, что рейтинг обновлен в базе
        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("5.00"));
    }
}

