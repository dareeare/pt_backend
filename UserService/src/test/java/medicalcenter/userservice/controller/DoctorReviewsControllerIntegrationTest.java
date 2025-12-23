package medicalcenter.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewCreateEditDto;
import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.DoctorReview;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.DoctorReviewRepository;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class DoctorReviewsControllerIntegrationTest {

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    }

    @Test
    void createReview_WhenValidData_ShouldCreateReviewAndReturn201() throws Exception {
        // Given
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                false
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        // When & Then
        mockMvc.perform(post("/api/doctor-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Отличный врач!"))
                .andExpect(jsonPath("$.isApproved").value(false))
                .andExpect(jsonPath("$.doctorId").value(testDoctor.getId().toString()))
                .andExpect(jsonPath("$.patientId").value(testPatient.getId().toString()))
                .andExpect(jsonPath("$.visitId").value(testVisit1.getId().toString()));

        // Проверяем, что отзыв сохранен в базе
        assertThat(doctorReviewRepository.findByVisitId(testVisit1.getId())).isPresent();
    }

    @Test
    void createReview_WhenApprovedReview_ShouldUpdateDoctorRating() throws Exception {
        // Given
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                true // одобрен сразу
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        // When
        mockMvc.perform(post("/api/doctor-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // Then - проверяем, что рейтинг врача обновлен
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void createReview_WhenInvalidData_ShouldReturn400() throws Exception {
        // Given - невалидные данные (рейтинг > 5)
        DoctorReviewCreateEditDto dto = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                10, // невалидный рейтинг
                "Отличный врач!",
                false
        );

        String requestBody = objectMapper.writeValueAsString(dto);

        // When & Then
        mockMvc.perform(post("/api/doctor-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReview_WhenReviewAlreadyExistsForVisit_ShouldReturn400() throws Exception {
        // Given - создаем первый отзыв
        DoctorReviewCreateEditDto dto1 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(),
                5,
                "Отличный врач!",
                false
        );

        String requestBody1 = objectMapper.writeValueAsString(dto1);
        mockMvc.perform(post("/api/doctor-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isCreated());

        // Пытаемся создать второй отзыв для того же визита
        DoctorReviewCreateEditDto dto2 = new DoctorReviewCreateEditDto(
                testPatient.getId(),
                testDoctor.getId(),
                testVisit1.getId(), // тот же визит
                4,
                "Хороший врач!",
                false
        );

        String requestBody2 = objectMapper.writeValueAsString(dto2);

        // When & Then
        mockMvc.perform(post("/api/doctor-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveReview_WhenReviewExists_ShouldApproveAndUpdateRating() throws Exception {
        // Given - создаем неодобренный отзыв
        DoctorReview review = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit1)
                .rating(5)
                .comment("Отличный врач!")
                .isApproved(false)
                .build();
        review = doctorReviewRepository.save(review);

        UUID reviewId = review.getId();

        // When
        mockMvc.perform(post("/api/doctor-reviews/{id}/approve", reviewId))
                .andExpect(status().isNoContent());

        // Then - проверяем, что отзыв одобрен
        DoctorReview approvedReview = doctorReviewRepository.findById(reviewId).orElseThrow();
        assertThat(approvedReview.getIsApproved()).isTrue();

        // Проверяем, что рейтинг врача обновлен
        Doctor updatedDoctor = doctorRepository.findById(testDoctor.getId()).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void approveReview_WhenReviewNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentReviewId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(post("/api/doctor-reviews/{id}/approve", nonExistentReviewId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAverageRating_WhenDoctorHasApprovedReviews_ShouldReturnAverage() throws Exception {
        // Given - создаем одобренные отзывы: 5, 4, 3 = среднее 4.0
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

        Visit visit3 = Visit.builder()
                .dateOfVisit(LocalDateTime.now().minusDays(1))
                .status("completed")
                .symptoms("Боль в груди")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        visit3 = visitRepository.save(visit3);

        DoctorReview review3 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(visit3)
                .rating(3)
                .comment("Средний врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review3);

        // When & Then
        mockMvc.perform(get("/api/doctor-reviews/doctor/{doctorId}/average-rating", testDoctor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(4.0));
    }

    @Test
    void getAverageRating_WhenDoctorHasNoApprovedReviews_ShouldReturnZero() throws Exception {
        // Given - нет одобренных отзывов

        // When & Then
        // Контроллер возвращает 0.0 вместо null для лучшей обработки
        mockMvc.perform(get("/api/doctor-reviews/doctor/{doctorId}/average-rating", testDoctor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(0.0));
    }
}

