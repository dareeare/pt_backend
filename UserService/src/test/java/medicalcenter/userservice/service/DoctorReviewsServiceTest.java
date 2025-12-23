package medicalcenter.userservice.service;

import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewCreateEditDto;
import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.DoctorReview;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.DoctorReviewRepository;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.repository.VisitRepository;
import medicalcenter.userservice.service.impl.DoctorReviewsService;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@Transactional
class DoctorReviewsServiceTest {

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
    private DoctorReviewsService doctorReviewsService;

    @Autowired
    private DoctorReviewRepository doctorReviewRepository;

    @Autowired
    private DoctorRepository doctorRepository;

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
    void save_WhenCreatingApprovedReview_ShouldUpdateDoctorRating() {
        // Given
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                true // одобрен сразу
        );

        // When
        doctorReviewsService.save(dto);

        // Then
        // Проверяем, что отзыв создан
        Optional<DoctorReview> savedReview = doctorReviewRepository.findByVisitId(testVisit1.getId());
        assertThat(savedReview).isPresent();
        assertThat(savedReview.get().getRating()).isEqualTo(5);
        assertThat(savedReview.get().getIsApproved()).isTrue();

        // Проверяем, что рейтинг врача обновлен
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void save_WhenCreatingUnapprovedReview_ShouldNotUpdateDoctorRating() {
        // Given
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                false // не одобрен
        );

        // When
        doctorReviewsService.save(dto);

        // Then
        // Проверяем, что отзыв создан
        Optional<DoctorReview> savedReview = doctorReviewRepository.findByVisitId(testVisit1.getId());
        assertThat(savedReview).isPresent();
        assertThat(savedReview.get().getIsApproved()).isFalse();

        // Проверяем, что рейтинг врача не изменился (остался 0)
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void save_WhenCreatingMultipleApprovedReviews_ShouldCalculateAverageRating() {
        // Given - создаем первый одобренный отзыв
        DoctorReviewCreateEditDto dto1 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                true
        );
        doctorReviewsService.save(dto1);

        // Создаем второй одобренный отзыв
        DoctorReviewCreateEditDto dto2 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit2.getId(),
                4,
                "Хороший врач!",
                true
        );

        // When
        doctorReviewsService.save(dto2);

        // Then
        // Проверяем, что оба отзыва созданы
        assertThat(doctorReviewRepository.findByVisitId(testVisit1.getId())).isPresent();
        assertThat(doctorReviewRepository.findByVisitId(testVisit2.getId())).isPresent();

        // Проверяем, что рейтинг врача равен среднему (4.5)
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("4.50"));
    }

    @Test
    void save_WhenVisitDoesNotBelongToDoctor_ShouldThrowException() {
        // Given - создаем другого врача
        Doctor anotherDoctor = Doctor.builder()
                .firstName("Мария")
                .lastName("Петрова")
                .specialty("Терапевт")
                .phone("80292345678")
                .email("petrova@clinic.com")
                .build();
        anotherDoctor = doctorRepository.save(anotherDoctor);

        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                anotherDoctor.getId(), // другой врач
                testVisit1.getId(), // визит принадлежит testDoctor
                5,
                "Отличный врач!",
                false
        );

        // When & Then
        assertThatThrownBy(() -> doctorReviewsService.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Visit does not belong to the specified doctor");
    }

    @Test
    void save_WhenVisitDoesNotBelongToPatient_ShouldThrowException() {
        // Given - создаем другого пациента
        Patient anotherPatient = Patient.builder()
                .firstName("Анна")
                .lastName("Козлова")
                .phone("80294567890")
                .email("kozlova@example.com")
                .gender("F")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();
        anotherPatient = patientRepository.save(anotherPatient);

        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                anotherPatient.getId(), // другой пациент
                testDoctor.getId(),
                testVisit1.getId(), // визит принадлежит testPatient
                5,
                "Отличный врач!",
                false
        );

        // When & Then
        assertThatThrownBy(() -> doctorReviewsService.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Visit does not belong to the specified patient");
    }

    @Test
    void save_WhenReviewAlreadyExistsForVisit_ShouldThrowException() {
        // Given - создаем первый отзыв
        DoctorReviewCreateEditDto dto1 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                false
        );
        doctorReviewsService.save(dto1);

        // Пытаемся создать второй отзыв для того же визита
        DoctorReviewCreateEditDto dto2 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(), // тот же визит
                4,
                "Хороший врач!",
                false
        );

        // When & Then
        assertThatThrownBy(() -> doctorReviewsService.save(dto2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Review already exists for visit id");
    }

    @Test
    void save_WhenDoctorNotFound_ShouldThrowException() {
        // Given
        UUID nonExistentDoctorId = UUID.randomUUID();
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                nonExistentDoctorId,
                testVisit1.getId(),
                5,
                "Отличный врач!",
                false
        );

        // When & Then
        assertThatThrownBy(() -> doctorReviewsService.save(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void save_WhenPatientNotFound_ShouldThrowException() {
        // Given
        UUID nonExistentPatientId = UUID.randomUUID();
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                nonExistentPatientId,
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                false
        );

        // When & Then
        assertThatThrownBy(() -> doctorReviewsService.save(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void save_WhenVisitNotFound_ShouldThrowException() {
        // Given
        UUID nonExistentVisitId = UUID.randomUUID();
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                nonExistentVisitId,
                5,
                "Отличный врач!",
                false
        );

        // When & Then
        assertThatThrownBy(() -> doctorReviewsService.save(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Visit not found");
    }

    @Test
    void approveReview_WhenApprovingReview_ShouldUpdateDoctorRating() {
        // Given - создаем неодобренный отзыв
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                false
        );
        doctorReviewsService.save(dto);

        // Создаем еще один одобренный отзыв для расчета среднего
        DoctorReviewCreateEditDto dto2 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit2.getId(),
                4,
                "Хороший врач!",
                true
        );
        doctorReviewsService.save(dto2);

        // Проверяем начальный рейтинг (только один одобренный отзыв)
        Doctor doctorBefore = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(doctorBefore.getRating()).isEqualByComparingTo(new BigDecimal("4.00"));

        // Получаем ID неодобренного отзыва
        Optional<DoctorReview> unapprovedReview = doctorReviewRepository.findByVisitId(testVisit1.getId());
        assertThat(unapprovedReview).isPresent();
        UUID reviewId = unapprovedReview.get().getId();

        // When - одобряем отзыв
        doctorReviewsService.approveReview(reviewId);

        // Then
        // Проверяем, что отзыв одобрен
        DoctorReview approvedReview = doctorReviewRepository.findById(reviewId).orElseThrow();
        assertThat(approvedReview.getIsApproved()).isTrue();

        // Проверяем, что рейтинг врача пересчитан (среднее 5 и 4 = 4.5)
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("4.50"));
    }

    @Test
    void delete_WhenDeletingApprovedReview_ShouldRecalculateDoctorRating() {
        // Given - создаем два одобренных отзыва
        DoctorReviewCreateEditDto dto1 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                true
        );
        doctorReviewsService.save(dto1);

        DoctorReviewCreateEditDto dto2 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit2.getId(),
                4,
                "Хороший врач!",
                true
        );
        doctorReviewsService.save(dto2);

        // Проверяем начальный рейтинг
        Doctor doctorBefore = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(doctorBefore.getRating()).isEqualByComparingTo(new BigDecimal("4.50"));

        // Получаем ID первого отзыва
        Optional<DoctorReview> review1 = doctorReviewRepository.findByVisitId(testVisit1.getId());
        assertThat(review1).isPresent();
        UUID reviewId = review1.get().getId();

        // When - удаляем первый отзыв
        doctorReviewsService.delete(reviewId);

        // Then
        // Проверяем, что отзыв удален
        assertThat(doctorReviewRepository.findById(reviewId)).isEmpty();

        // Проверяем, что рейтинг врача пересчитан (остался только один отзыв с рейтингом 4)
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("4.00"));
    }

    @Test
    void delete_WhenDeletingUnapprovedReview_ShouldNotRecalculateDoctorRating() {
        // Given - создаем один одобренный отзыв
        DoctorReviewCreateEditDto dto1 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                true
        );
        doctorReviewsService.save(dto1);

        // Создаем неодобренный отзыв
        DoctorReviewCreateEditDto dto2 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit2.getId(),
                1,
                "Плохой врач!",
                false
        );
        doctorReviewsService.save(dto2);

        // Проверяем начальный рейтинг
        Doctor doctorBefore = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(doctorBefore.getRating()).isEqualByComparingTo(new BigDecimal("5.00"));

        // Получаем ID неодобренного отзыва
        Optional<DoctorReview> unapprovedReview = doctorReviewRepository.findByVisitId(testVisit2.getId());
        assertThat(unapprovedReview).isPresent();
        UUID reviewId = unapprovedReview.get().getId();

        // When - удаляем неодобренный отзыв
        doctorReviewsService.delete(reviewId);

        // Then
        // Проверяем, что отзыв удален
        assertThat(doctorReviewRepository.findById(reviewId)).isEmpty();

        // Проверяем, что рейтинг врача не изменился (неодобренные отзывы не учитываются)
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("5.00"));
    }
}

