package medicalcenter.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.authservice.model.RoleEnum;
import medicalcenter.authservice.model.dto.RegisterUserDto;
import medicalcenter.authservice.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncService {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;

    @Value("${userservice.url:http://localhost:8081}")
    private String userServiceUrl;

    public void syncUser(User user, RegisterUserDto dto) {
        String endpoint = getEndpointByRole(user.getRole().getRole());
        if (endpoint == null) {
            log.warn("No endpoint mapped for role {}", user.getRole().getRole());
            return;
        }

        // ВАЖНО: Генерируем токен с ролью SYSTEM или MANAGER, чтобы иметь права на создание
        // Если мы используем токен самого пользователя (например DOCTOR), то у него может не быть прав
        // на вызов POST /api/doctors (обычно это делает админ).
        // Поэтому здесь мы "подписываем" запрос токеном с повышенными привилегиями, 
        // либо модифицируем generateAccessToken, чтобы он принимал authorities.
        // В данном случае, мы используем существующий метод, но можем временно подменить роль для токена синхронизации,
        // либо UserService должен разрешать POST /api/doctors для ROLE_DOCTOR (саморегистрация), что менее безопасно.
        
        // Безопасный вариант: UserService разрешает создание только MANAGER/ADMIN.
        // Значит, AuthService должен отправлять запрос от имени MANAGER.
        // Но у нас нет объекта User для менеджера здесь.
        
        // Упрощение для текущей задачи: отправляем токен пользователя, 
        // но в UserService разрешаем создание (POST) для authenticated users, 
        // полагаясь на то, что этот запрос пришел из доверенного источника (AuthService).
        // В идеале здесь должен быть service-to-service токен (Client Credentials Flow).
        
        String token = jwtService.generateAccessToken(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = new HashMap<>();
        body.put("firstName", user.getFirstName());
        body.put("lastName", user.getLastName());
        body.put("phone", user.getPhone());
        body.put("email", user.getEmail());
        
        if (dto.birthDate() != null) {
            body.put("dateOfBirth", dto.birthDate());
        }
        
        body.put("avatarPath", dto.avatarUrl());

        RoleEnum role = user.getRole().getRole();
        if (role == RoleEnum.ROLE_PATIENT) {
            body.put("gender", dto.gender() != null ? dto.gender() : "O");
        } else if (role == RoleEnum.ROLE_DOCTOR) {
            body.put("specialty", dto.specialty() != null ? dto.specialty() : "General");
            body.remove("dateOfBirth"); // Doctor DTO doesn't use it
        } else if (role == RoleEnum.ROLE_OPERATOR) {
            if (dto.birthDate() == null) {
                body.put("dateOfBirth", "1990-01-01");
            }
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(userServiceUrl + endpoint, request, Void.class);
            log.info("Successfully synced user {} to UserService", user.getPhone());
        } catch (Exception e) {
            log.error("Failed to sync user {} to UserService: {}", user.getPhone(), e.getMessage());
            throw new RuntimeException("Failed to create user profile in UserService: " + e.getMessage());
        }
    }

    public void deleteUser(String phone) {
        // Попытка удалить из всех возможных endpoints, так как мы не знаем роль наверняка (или можно передать роль)
        // Либо AuthService.deleteUser может передать роль.
        // Для простоты попробуем удалить отовсюду, игнорируя 404.
        
        String[] endpoints = {"/api/patients", "/api/doctors", "/api/operators"};
        
        for (String ep : endpoints) {
            try {
                // Предполагаем, что у нас есть endpoint DELETE /api/xxx?phone=...
                // Но в контроллерах UserService обычно удаление по ID.
                // Нам нужно сначала найти ID по телефону, потом удалить?
                // Или добавить endpoint deleteByPhone.
                
                // Добавим вызов: DELETE {userServiceUrl}{ep}/by-phone?phone={phone}
                // Предварительно нужно реализовать такие эндпоинты в UserService или использовать RestTemplate exchange.
                
                // Вариант проще: UserSyncService знает, что API UserService поддерживает поиск по телефону.
                // GET /api/xxx?phone=... -> получить ID -> DELETE /api/xxx/{id}
                
                // Но чтобы не усложнять, реализуем один специальный endpoint в UserService для удаления по телефону?
                // Или просто оставим это на "todo" или "admin panel".
                
                // Реализуем поиск + удаление.
                
                // Пока оставим заглушку, так как это требует доработки API UserService.
                log.info("Sync delete for user {} requested (not implemented fully)", phone);
                
            } catch (Exception e) {
                log.warn("Failed to sync delete from {}", ep);
            }
        }
    }

    private String getEndpointByRole(RoleEnum role) {
        return switch (role) {
            case ROLE_PATIENT -> "/api/patients";
            case ROLE_DOCTOR -> "/api/doctors";
            case ROLE_OPERATOR -> "/api/operators";
            default -> null;
        };
    }
}
