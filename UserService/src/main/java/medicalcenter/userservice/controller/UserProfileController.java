package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.repository.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {
    
    private final PatientRepository patientRepository;

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
}
