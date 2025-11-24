package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.DoctorReview;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для DoctorReviewRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class DoctorReviewRepositoryParameterizedTests {

    @Autowired
    private DoctorReviewRepository repository;

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

    @BeforeEach
    void setUp() {
        testDoctor = Doctor.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .specialty("Кардиолог")
                .phone("80291234567")
                .email("ivanov@clinic.com")
                .build();
        testDoctor = doctorRepository.save(testDoctor);

        testPatient = Patient.builder()
                .firstName("Петр")
                .lastName("Сидоров")
                .phone("80293456789")
                .email("sidorov@example.com")
                .gender("M")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
        testPatient = patientRepository.save(testPatient);

        testVisit1 = Visit.builder()
                .dateOfVisit(LocalDateTime.of(2024, 1, 15, 10, 0))
                .status("completed")
                .symptoms("Test symptoms 1")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        testVisit1 = visitRepository.save(testVisit1);

        testVisit2 = Visit.builder()
                .dateOfVisit(LocalDateTime.of(2024, 2, 20, 14, 0))
                .status("completed")
                .symptoms("Test symptoms 2")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        testVisit2 = visitRepository.save(testVisit2);
    }

    /**
     * Источник данных для тестирования создания отзывов
     */
    private static Stream<Arguments> provideDoctorReviewDataForCreate() {
        return Stream.of(
                Arguments.of(5, "Отличный врач!", true, false),
                Arguments.of(4, "Хороший специалист", true, false),
                Arguments.of(3, "Нормально", false, false),
                Arguments.of(5, "Профессионал своего дела", true, false),
                Arguments.of(4, "Рекомендую", true, false)
        );
    }

    /**
     * Параметризованный тест для создания отзыва (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create review: rating={0}, comment={1}")
    @MethodSource("provideDoctorReviewDataForCreate")
    @DisplayName("save() should persist doctor review with different data sets")
    void testSaveDoctorReview_Parameterized(Integer rating, String comment, Boolean isApproved, Boolean isEdited) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(rating)
                .comment(comment)
                .isApproved(isApproved)
                .isEdited(isEdited)
                .build();

        // Act
        DoctorReview saved = repository.save(review);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRating()).isEqualTo(rating);
        assertThat(saved.getComment()).isEqualTo(comment);
        assertThat(saved.getIsApproved()).isEqualTo(isApproved);
        assertThat(saved.getIsEdited()).isEqualTo(isEdited);
        assertThat(saved.getPatient().getId()).isEqualTo(testPatient.getId());
        assertThat(saved.getDoctor().getId()).isEqualTo(testDoctor.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    /**
     * Параметризованный тест для чтения отзыва (READ)
     */
    @ParameterizedTest(name = "[{index}] Read review by ID: {1}")
    @MethodSource("provideDoctorReviewDataForCreate")
    @DisplayName("findById() should retrieve doctor review with different data sets")
    void testFindById_Parameterized(Integer rating, String comment, Boolean isApproved, Boolean isEdited) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(rating)
                .comment(comment)
                .isApproved(isApproved)
                .isEdited(isEdited)
                .build();
        DoctorReview saved = repository.save(review);

        // Act
        Optional<DoctorReview> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getRating()).isEqualTo(rating);
        assertThat(found.get().getComment()).isEqualTo(comment);
        assertThat(found.get().getIsApproved()).isEqualTo(isApproved);
    }

    /**
     * Источник данных для тестирования обновления отзывов
     */
    private static Stream<Arguments> provideDoctorReviewDataForUpdate() {
        return Stream.of(
                Arguments.of(
                        3, "Old comment 1", false, false,
                        5, "Updated comment 1", true, true
                ),
                Arguments.of(
                        4, "Old comment 2", true, false,
                        5, "Updated comment 2", true, true
                ),
                Arguments.of(
                        2, "Old comment 3", false, false,
                        4, "Updated comment 3", true, true
                )
        );
    }

    /**
     * Параметризованный тест для обновления отзыва (UPDATE)
     */
    @ParameterizedTest(name = "[{index}] Update review: rating {0} -> {4}")
    @MethodSource("provideDoctorReviewDataForUpdate")
    @DisplayName("updateById() should update doctor review fields with different data sets")
    void testUpdateById_Parameterized(
            Integer oldRating, String oldComment, Boolean oldIsApproved, Boolean oldIsEdited,
            Integer newRating, String newComment, Boolean newIsApproved, Boolean newIsEdited) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(oldRating)
                .comment(oldComment)
                .isApproved(oldIsApproved)
                .isEdited(oldIsEdited)
                .build();
        DoctorReview saved = repository.save(review);
        UUID reviewId = saved.getId();

        // Act
        int updated = repository.updateById(
                reviewId,
                testPatient.getId(),
                testDoctor.getId(),
                testVisit2.getId(),
                newRating,
                newComment,
                newIsApproved,
                newIsEdited
        );

        // Assert
        assertThat(updated).isEqualTo(1);

        Optional<DoctorReview> updatedReview = repository.findById(reviewId);
        assertThat(updatedReview).isPresent();
        assertThat(updatedReview.get().getRating()).isEqualTo(newRating);
        assertThat(updatedReview.get().getComment()).isEqualTo(newComment);
        assertThat(updatedReview.get().getIsApproved()).isEqualTo(newIsApproved);
        assertThat(updatedReview.get().getIsEdited()).isEqualTo(newIsEdited);
    }

    /**
     * Параметризованный тест для удаления отзыва (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete review: rating={0}")
    @MethodSource("provideDoctorReviewDataForCreate")
    @DisplayName("deleteById() should remove doctor review with different data sets")
    void testDeleteById_Parameterized(Integer rating, String comment, Boolean isApproved, Boolean isEdited) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(rating)
                .comment(comment)
                .isApproved(isApproved)
                .isEdited(isEdited)
                .build();
        DoctorReview saved = repository.save(review);
        UUID reviewId = saved.getId();

        assertThat(repository.findById(reviewId)).isPresent();

        // Act
        repository.deleteById(reviewId);

        // Assert
        assertThat(repository.findById(reviewId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска по ID доктора
     */
    @ParameterizedTest
    @CsvSource({
            "5, Отличный врач!",
            "4, Хороший специалист",
            "3, Нормально"
    })
    @DisplayName("findByDoctorId() should find reviews by doctor")
    void testFindByDoctorId_Parameterized(Integer rating, String comment) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(rating)
                .comment(comment)
                .isApproved(true)
                .isEdited(false)
                .build();
        repository.save(review);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<DoctorReview> found = repository.findByDoctorId(testDoctor.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(r -> r.getRating().equals(rating));
    }

    /**
     * Параметризованный тест для поиска по ID пациента
     */
    @ParameterizedTest
    @CsvSource({
            "5, Отличный врач!",
            "4, Хороший специалист",
            "3, Нормально"
    })
    @DisplayName("findByPatientId() should find reviews by patient")
    void testFindByPatientId_Parameterized(Integer rating, String comment) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(rating)
                .comment(comment)
                .isApproved(true)
                .isEdited(false)
                .build();
        repository.save(review);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<DoctorReview> found = repository.findByPatientId(testPatient.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(r -> r.getComment().equals(comment));
    }

    /**
     * Параметризованный тест для поиска по ID визита
     */
    @ParameterizedTest
    @CsvSource({
            "5, Отличный врач!, true",
            "4, Хороший специалист, true",
            "3, Нормально, false"
    })
    @DisplayName("findByVisitId() should find review by visit")
    void testFindByVisitId_Parameterized(Integer rating, String comment, Boolean isApproved) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(rating)
                .comment(comment)
                .isApproved(isApproved)
                .isEdited(false)
                .build();
        repository.save(review);

        // Act
        Optional<DoctorReview> found = repository.findByVisitId(testVisit1.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getRating()).isEqualTo(rating);
        assertThat(found.get().getComment()).isEqualTo(comment);
    }

    /**
     * Параметризованный тест для поиска одобренных отзывов
     */
    @ParameterizedTest
    @CsvSource({
            "5, Отличный врач!",
            "4, Хороший специалист",
            "5, Рекомендую"
    })
    @DisplayName("findApprovedByDoctorId() should find approved reviews by doctor")
    void testFindApprovedByDoctorId_Parameterized(Integer rating, String comment) {
        // Arrange
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(rating)
                .comment(comment)
                .isApproved(true)
                .isEdited(false)
                .build();
        repository.save(review);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<DoctorReview> found = repository.findApprovedByDoctorId(testDoctor.getId(), pageable);

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found).allMatch(r -> r.getIsApproved());
    }

    /**
     * Параметризованный тест для расчета среднего рейтинга
     */
    @ParameterizedTest
    @CsvSource({
            "5, 4, 5, 4.67",
            "3, 4, 5, 4.0",
            "5, 5, 5, 5.0"
    })
    @DisplayName("findAverageRatingByDoctorId() should calculate average rating")
    void testFindAverageRating_Parameterized(Integer rating1, Integer rating2, Integer rating3, Double expectedAvg) {
        // Arrange
        Visit visit2 = Visit.builder()
                .dateOfVisit(LocalDateTime.of(2024, 2, 15, 10, 0))
                .status("completed")
                .symptoms("Test 2")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        visit2 = visitRepository.save(visit2);

        Visit visit3 = Visit.builder()
                .dateOfVisit(LocalDateTime.of(2024, 3, 15, 10, 0))
                .status("completed")
                .symptoms("Test 3")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        visit3 = visitRepository.save(visit3);

        repository.save(DoctorReview.builder()
                .patient(testPatient).doctor(testDoctor).visit(testVisit1)
                .rating(rating1).comment("Test 1").isApproved(true).isEdited(false).build());
        repository.save(DoctorReview.builder()
                .patient(testPatient).doctor(testDoctor).visit(visit2)
                .rating(rating2).comment("Test 2").isApproved(true).isEdited(false).build());
        repository.save(DoctorReview.builder()
                .patient(testPatient).doctor(testDoctor).visit(visit3)
                .rating(rating3).comment("Test 3").isApproved(true).isEdited(false).build());

        // Act
        Double avgRating = repository.findAverageRatingByDoctorId(testDoctor.getId());

        // Assert
        assertThat(avgRating).isNotNull();
        assertThat(avgRating).isCloseTo(expectedAvg, org.assertj.core.data.Offset.offset(0.1));
    }
}

