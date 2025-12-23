package medicalcenter.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.patient.PatientCreateEditDto;
import medicalcenter.userservice.model.dto.patient.ProfileUpdateDto;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.service.impl.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile", description = "API для управления профилем текущего пользователя")
public class UserProfileController {
    
    private final PatientRepository patientRepository;
    private final PatientService patientService;

    /**
     * Получить данные текущего авторизованного пользователя
     * Возвращает patient_id по phone из JWT токена
     * Endpoint: GET /api/user/me
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("User not authenticated");
                return ResponseEntity.status(401).build();
            }

            String phone = authentication.getName(); // Phone из JWT (subject)
            log.info("Getting patient data for phone: {}", phone);

            // Ищем пациента по номеру телефона
            Optional<Patient> patientOpt = patientRepository.findByPhone(phone);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", phone);
            userData.put("authenticated", true);
            userData.put("authorities", authentication.getAuthorities());
            
            if (patientOpt.isPresent()) {
                Patient patient = patientOpt.get();
                userData.put("patientId", patient.getId().toString());
                userData.put("userId", patient.getId().toString());
                userData.put("firstName", patient.getFirstName());
                userData.put("lastName", patient.getLastName());
                userData.put("email", patient.getEmail());
                userData.put("phone", patient.getPhone());
                
                log.info("✅ Found patient: {} {} with UUID: {}", 
                    patient.getFirstName(), 
                    patient.getLastName(), 
                    patient.getId());
            } else {
                log.warn("❌ Patient not found for phone: {}", phone);
                userData.put("error", "Patient not found for phone: " + phone);
            }
            
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            log.error("❌ Error getting current user", e);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorData);
        }
    }

    /**
     * Обновить данные текущего авторизованного пользователя
     * Endpoint: PUT /api/user/me
     */
    @Operation(
            summary = "Обновить данные текущего пользователя",
            description = "Обновляет информацию о текущем авторизованном пользователе (пациенте)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления")
    })
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @RequestBody @Valid PatientCreateEditDto dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("User not authenticated");
                return ResponseEntity.status(401).build();
            }

            String phone = authentication.getName(); // Phone из JWT (subject)
            log.info("Updating patient data for phone: {}", phone);

            // Ищем пациента по номеру телефона
            Optional<Patient> patientOpt = patientRepository.findByPhone(phone);
            
            if (patientOpt.isEmpty()) {
                log.warn("❌ Patient not found for phone: {}", phone);
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "Patient not found for phone: " + phone);
                return ResponseEntity.status(404).body(errorData);
            }

            Patient patient = patientOpt.get();
            UUID patientId = patient.getId();
            
            // Обновляем данные пациента
            patientService.update(patientId, dto);
            
            log.info("✅ Patient {} {} updated successfully", dto.firstName(), dto.lastName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User profile updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error updating current user", e);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorData);
        }
    }

    /**
     * Получить данные профиля текущего авторизованного пользователя
     * Endpoint: GET /api/user/profile
     * Возвращает данные в формате, совместимом с фронтендом
     */
    @Operation(
            summary = "Получить данные профиля пользователя",
            description = "Возвращает данные профиля текущего авторизованного пользователя (пациента)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные профиля успешно получены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("User not authenticated");
                return ResponseEntity.status(401).build();
            }

            String phone = authentication.getName(); // Phone из JWT (subject)
            log.info("Getting user profile for phone: {}", phone);

            // Ищем пациента по номеру телефона
            Optional<Patient> patientOpt = patientRepository.findByPhone(phone);
            
            if (patientOpt.isEmpty()) {
                log.warn("❌ Patient not found for phone: {}", phone);
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "Patient not found for phone: " + phone);
                return ResponseEntity.status(404).body(errorData);
            }

            Patient patient = patientOpt.get();
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("firstName", patient.getFirstName() != null ? patient.getFirstName() : "");
            profileData.put("lastName", patient.getLastName() != null ? patient.getLastName() : "");
            profileData.put("email", patient.getEmail() != null ? patient.getEmail() : "");
            profileData.put("phone", patient.getPhone() != null ? patient.getPhone() : "");
            
            // Преобразуем LocalDate в String для birthDate
            if (patient.getDateOfBirth() != null) {
                profileData.put("birthDate", patient.getDateOfBirth().toString());
            }
            
            // Преобразуем avatarPath в avatarUrl
            if (patient.getAvatarPath() != null) {
                profileData.put("avatarUrl", patient.getAvatarPath());
            }
            
            log.info("✅ Profile data retrieved for patient: {} {}", 
                patient.getFirstName(), 
                patient.getLastName());
            
            return ResponseEntity.ok(profileData);
        } catch (Exception e) {
            log.error("❌ Error getting user profile", e);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorData);
        }
    }

    /**
     * Обновить данные профиля текущего авторизованного пользователя
     * Endpoint: PUT /api/user/profile
     * Принимает данные в формате, совместимом с фронтендом
     */
    @Operation(
            summary = "Обновить данные профиля пользователя",
            description = "Обновляет информацию о профиле текущего авторизованного пользователя (пациента)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные профиля успешно обновлены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления")
    })
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @Parameter(description = "Обновленные данные профиля", required = true)
            @RequestBody @Valid ProfileUpdateDto dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("User not authenticated");
                return ResponseEntity.status(401).build();
            }

            String phone = authentication.getName(); // Phone из JWT (subject)
            log.info("Updating user profile for phone: {}", phone);

            // Ищем пациента по номеру телефона
            Optional<Patient> patientOpt = patientRepository.findByPhone(phone);
            
            if (patientOpt.isEmpty()) {
                log.warn("❌ Patient not found for phone: {}", phone);
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "Patient not found for phone: " + phone);
                return ResponseEntity.status(404).body(errorData);
            }

            Patient patient = patientOpt.get();
            UUID patientId = patient.getId();
            
            // Обрабатываем phone: если передан и не пустой, используем его, иначе сохраняем существующий
            String phoneToUse = patient.getPhone(); // По умолчанию используем существующий
            if (dto.phone() != null && !dto.phone().trim().isEmpty()) {
                // Валидируем формат телефона, если он передан
                String phonePattern = "^80(29|17|33|44|25)\\d{7}$";
                if (!dto.phone().matches(phonePattern)) {
                    log.warn("Invalid phone format: {}", dto.phone());
                    Map<String, Object> errorData = new HashMap<>();
                    errorData.put("error", "Phone must have format 80(29|17|33|44|25) followed by 7 digits");
                    return ResponseEntity.status(400).body(errorData);
                }
                phoneToUse = dto.phone();
            }
            
            // Преобразуем ProfileUpdateDto в PatientCreateEditDto
            // Сохраняем существующие значения для полей, которые не переданы в dto
            PatientCreateEditDto updateDto = new PatientCreateEditDto(
                    dto.firstName(),
                    dto.lastName(),
                    patient.getMiddleName(), // Сохраняем существующее отчество
                    phoneToUse, // Используем обработанный phone
                    patient.getEmail(), // Сохраняем существующий email
                    dto.birthDate() != null ? dto.birthDate() : patient.getDateOfBirth(), // Используем переданную дату или существующую
                    patient.getGender() != null ? patient.getGender() : "M", // Сохраняем существующий пол или используем значение по умолчанию
                    patient.getAvatarPath() // Сохраняем существующий путь к аватару
            );
            
            // Обновляем данные пациента
            patientService.update(patientId, updateDto);
            
            log.info("✅ User profile updated successfully for patient: {} {}", 
                dto.firstName(), 
                dto.lastName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User profile updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error updating user profile", e);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorData);
        }
    }
}
