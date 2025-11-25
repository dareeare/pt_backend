package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.privatechat.PrivateChatMessageDto;
import medicalcenter.userservice.service.PrivateChatService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/private/chat")
@RequiredArgsConstructor
@Slf4j
public class PrivateChatRestController {

    private final PrivateChatService privateChatService;

    @GetMapping("/messages/{otherUser}")
    public ResponseEntity<List<PrivateChatMessageDto>> getChatMessages(@PathVariable String otherUser) {
        // USER уровень доступа (включая анонимных пользователей)
        if (!hasUserAccess()) {
            log.warn("❌ Доступ запрещен: недостаточно прав для чтения чата");
            return ResponseEntity.status(403).build();
        }

        String currentUser = getCurrentUsername();
        if (!hasAccessToChat(currentUser, otherUser)) {
            log.warn("❌ Доступ запрещен: {} пытается получить чат с {}", currentUser, otherUser);
            return ResponseEntity.status(403).build();
        }

        log.debug("Getting chat messages between {} and {}", currentUser, otherUser);
        List<PrivateChatMessageDto> messages = privateChatService.getChatMessages(otherUser);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{otherUser}/paginated")
    public ResponseEntity<Page<PrivateChatMessageDto>> getChatMessagesPaginated(
            @PathVariable String otherUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        // USER уровень доступа (включая анонимных пользователей)
        if (!hasUserAccess()) {
            log.warn("❌ Доступ запрещен: недостаточно прав для чтения пагинированного чата");
            return ResponseEntity.status(403).build();
        }

        String currentUser = getCurrentUsername();
        if (!hasAccessToChat(currentUser, otherUser)) {
            log.warn("❌ Доступ запрещен: {} пытается получить пагинированный чат с {}", currentUser, otherUser);
            return ResponseEntity.status(403).build();
        }

        log.debug("Getting paginated chat messages between {} and {}", currentUser, otherUser);
        Page<PrivateChatMessageDto> messages = privateChatService.getChatMessagesPaginated(otherUser, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages/{sender}/read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable String sender) {
        // USER уровень доступа (только аутентифицированные)
        if (!hasUserAccess() || isAnonymous()) {
            log.warn("❌ Доступ запрещен: аноним не может отмечать сообщения как прочитанные");
            return ResponseEntity.status(401).build();
        }

        String currentUser = getCurrentUsername();
        log.debug("Marking messages as read from {} to {}", sender, currentUser);
        privateChatService.markMessagesAsRead(sender);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadMessageCount() {
        // USER уровень доступа (только аутентифицированные)
        if (!hasUserAccess() || isAnonymous()) {
            log.warn("❌ Доступ запрещен: аноним не может получать количество непрочитанных");
            return ResponseEntity.status(401).build();
        }

        long count = privateChatService.getUnreadMessageCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/unread/count/{sender}")
    public ResponseEntity<Long> getUnreadMessageCountFromSender(@PathVariable String sender) {
        // USER уровень доступа (только аутентифицированные)
        if (!hasUserAccess() || isAnonymous()) {
            log.warn("❌ Доступ запрещен: аноним не может получать количество непрочитанных от отправителя");
            return ResponseEntity.status(401).build();
        }

        long count = privateChatService.getUnreadMessageCountFromUser(sender);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/partners")
    public ResponseEntity<List<String>> getChatPartners() {
        // USER уровень доступа (только аутентифицированные)
        if (!hasUserAccess() || isAnonymous()) {
            log.warn("❌ Доступ запрещен: аноним не может получать список чат-партнеров");
            return ResponseEntity.status(401).build();
        }

        List<String> partners = privateChatService.getChatPartners();
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/online/{username}")
    public ResponseEntity<Boolean> isUserOnline(@PathVariable String username) {
        // USER уровень доступа (включая анонимных пользователей)
        if (!hasUserAccess()) {
            log.warn("❌ Доступ запрещен: недостаточно прав для проверки онлайн-статуса");
            return ResponseEntity.status(403).build();
        }

        String currentUser = getCurrentUsername();
        log.debug("User {} checking online status of {}", currentUser, username);
        
        boolean isOnline = privateChatService.isUserOnline(username);
        return ResponseEntity.ok(isOnline);
    }

    @GetMapping("/room-id/{otherUser}")
    public ResponseEntity<String> getChatRoomId(@PathVariable String otherUser) {
        // USER уровень доступа (включая анонимных пользователей)
        if (!hasUserAccess()) {
            log.warn("❌ Доступ запрещен: недостаточно прав для получения roomId");
            return ResponseEntity.status(403).build();
        }

        String currentUser = getCurrentUsername();
        String roomId = privateChatService.generateChatRoomId(currentUser, otherUser);
        log.debug("Generated roomId for {} and {}: {}", currentUser, otherUser, roomId);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchUsers(@RequestParam String query) {
        // USER уровень доступа (только аутентифицированные)
        if (!hasUserAccess() || isAnonymous()) {
            log.warn("❌ Доступ запрещен: аноним не может искать пользователей");
            return ResponseEntity.status(401).build();
        }

        String currentUser = getCurrentUsername();
        log.debug("User {} searching users with query: {}", currentUser, query);
        // Временная реализация - возвращаем пустой список
        // В реальном приложении здесь будет поиск по базе данных
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startChat(@RequestBody Map<String, String> request) {
        // USER уровень доступа (только аутентифицированные)
        if (!hasUserAccess() || isAnonymous()) {
            log.warn("❌ Доступ запрещен: аноним не может начинать чаты");
            return ResponseEntity.status(401).build();
        }

        String currentUser = getCurrentUsername();
        String username = request.get("username");
        log.debug("Starting chat between {} and {}", currentUser, username);
        
        // Временная реализация - просто возвращаем roomId
        String roomId = privateChatService.generateChatRoomId(currentUser, username);
        return ResponseEntity.ok(Map.of("roomId", roomId, "status", "started"));
    }

    @GetMapping("/online-users")
    public ResponseEntity<Map<String, Object>> getOnlineUsers() {
        // USER уровень доступа (включая анонимных пользователей)
        if (!hasUserAccess()) {
            log.warn("❌ Доступ запрещен: недостаточно прав для просмотра онлайн пользователей");
            return ResponseEntity.status(403).build();
        }

        String currentUser = getCurrentUsername();
        log.debug("User {} requesting online users list", currentUser);
        
        Set<String> onlineUsers = privateChatService.getOnlineUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("users", onlineUsers);
        response.put("count", onlineUsers.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadAttachment(@RequestParam("file") MultipartFile file) {
        // USER уровень доступа (только аутентифицированные)
        if (!hasUserAccess() || isAnonymous()) {
            log.warn("❌ Доступ запрещен: аноним не может загружать файлы");
            return ResponseEntity.status(401).build();
        }

        String currentUser = getCurrentUsername();
        log.debug("User {} uploading file: {}", currentUser, file.getOriginalFilename());
        
        // Временная реализация - возвращаем фиктивный URL
        // В реальном приложении здесь будет загрузка файла в облачное хранилище
        String fileUrl = "/uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }

    // Вспомогательные методы для проверки доступа

    /**
     * Получает текущее имя пользователя (аутентифицированного или анонимного)
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }

    /**
     * Проверяет, имеет ли пользователь уровень доступа USER
     * - Аутентифицированные пользователи с ролями: ROLE_USER, ROLE_PATIENT, ROLE_DOCTOR, ROLE_OPERATOR, ROLE_ADMIN
     * - Анонимные пользователи также считаются USER уровня
     */
    private boolean hasUserAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Если пользователь не аутентифицирован, считаем его USER уровня
        if (auth == null || !auth.isAuthenticated()) {
            return true;
        }

        // Проверяем роли пользователя
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            // USER уровень включает все роли, так как все пользователи могут использовать чат
            if (role.equals("ROLE_USER") || role.equals("ROLE_PATIENT") || 
                role.equals("ROLE_DOCTOR") || role.equals("ROLE_OPERATOR") || 
                role.equals("ROLE_ADMIN") || role.equals("ROLE_ANONYMOUS")) {
                return true;
            }
        }

        log.warn("❌ У пользователя {} недостаточно прав. Роли: {}", 
                auth.getName(), authorities);
        return false;
    }

    /**
     * Проверяет, является ли пользователь анонимным
     */
    private boolean isAnonymous() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null || 
               !auth.isAuthenticated() || 
               "anonymous".equals(auth.getName()) ||
               hasRole(auth, "ROLE_ANONYMOUS");
    }

    /**
     * Проверяет, имеет ли пользователь доступ к чату
     * - Все USER уровня имеют доступ к своим чатам
     * - Анонимные пользователи имеют доступ только к чатам, где их имя совпадает с участником
     */
    private boolean hasAccessToChat(String currentUser, String otherUser) {
        // Аутентифицированные пользователи USER уровня имеют полный доступ к своим чатам
        if (!isAnonymous()) {
            return true;
        }
        
        // Анонимные пользователи имеют доступ только если их имя совпадает с одним из участников
        boolean hasAccess = currentUser.equals(otherUser);
        log.debug("Анонимный доступ к чату {}: {}", otherUser, hasAccess ? "разрешен" : "запрещен");
        return hasAccess;
    }

    /**
     * Проверяет, имеет ли пользователь указанную роль
     */
    private boolean hasRole(Authentication auth, String role) {
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}