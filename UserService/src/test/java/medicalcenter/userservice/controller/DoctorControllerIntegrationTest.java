package medicalcenter.userservice.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class DoctorControllerIntegrationTest {

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
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorReviewRepository doctorReviewRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    private Doctor testDoctor;
    private Patient testPatient;
    private Visit testVisit;

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

        // Создание тестового визита
        testVisit = Visit.builder()
                .dateOfVisit(LocalDateTime.now().minusDays(5))
                .status("completed")
                .symptoms("Головная боль")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        testVisit = visitRepository.save(testVisit);
    }

    @Test
    void getRating_WhenDoctorExists_ShouldReturnRating() throws Exception {
        // Given
        UUID doctorId = testDoctor.getId();
        testDoctor.setRating(new BigDecimal("4.5"));
        testDoctor = doctorRepository.save(testDoctor);

        // When & Then
        mockMvc.perform(get("/api/doctors/{id}/rating", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(4.5));
    }

    @Test
    void getRating_WhenDoctorHasNoRating_ShouldReturnZero() throws Exception {
        // Given
        UUID doctorId = testDoctor.getId();

        // When & Then
        mockMvc.perform(get("/api/doctors/{id}/rating", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(0));
    }

    @Test
    void getRating_WhenDoctorNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentDoctorId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/doctors/{id}/rating", nonExistentDoctorId))
                .andExpect(status().isNotFound());
    }

    @Test
    void recalculateRating_WhenDoctorHasApprovedReviews_ShouldRecalculateAndReturnNewRating() throws Exception {
        // Given
        UUID doctorId = testDoctor.getId();

        // Создаем одобренные отзывы: 5, 4, 3 = среднее 4.0
        DoctorReview review1 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(testVisit)
                .rating(5)
                .comment("Отличный врач")
                .isApproved(true)
                .build();
        doctorReviewRepository.save(review1);

        Visit visit2 = Visit.builder()
                .dateOfVisit(LocalDateTime.now().minusDays(3))
                .status("completed")
                .symptoms("Высокое давление")
                .patient(testPatient)
                .doctor(testDoctor)
                .build();
        visit2 = visitRepository.save(visit2);

        DoctorReview review2 = DoctorReview.builder()
                .patient(testPatient)
                .doctor(testDoctor)
                .visit(visit2)
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
        mockMvc.perform(post("/api/doctors/{id}/rating/recalculate", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(4.0));

        // Проверяем, что рейтинг обновлен в базе
        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(new BigDecimal("4.00"));
    }

    @Test
    void recalculateRating_WhenDoctorHasNoApprovedReviews_ShouldReturnZero() throws Exception {
        // Given
        UUID doctorId = testDoctor.getId();

        // When & Then
        mockMvc.perform(post("/api/doctors/{id}/rating/recalculate", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(0));

        // Проверяем, что рейтинг обновлен в базе
        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElseThrow();
        assertThat(updatedDoctor.getRating()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void recalculateRating_WhenDoctorNotFound_ShouldReturn404() throws Exception {
        // Given
        UUID nonExistentDoctorId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(post("/api/doctors/{id}/rating/recalculate", nonExistentDoctorId))
                .andExpect(status().isNotFound());
    }
}

