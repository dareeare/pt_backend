package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.privatechat.PrivateChatMessageDto;
import medicalcenter.userservice.service.PrivateChatService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrivateChatMessageDto>> getChatMessages(
            Principal principal,
            @PathVariable String otherUser) {
        log.debug("Getting chat messages between {} and {}", principal.getName(), otherUser);
        List<PrivateChatMessageDto> messages = privateChatService.getChatMessages(principal.getName(), otherUser);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{otherUser}/paginated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PrivateChatMessageDto>> getChatMessagesPaginated(
            Principal principal,
            @PathVariable String otherUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.debug("Getting paginated chat messages between {} and {}", principal.getName(), otherUser);
        Page<PrivateChatMessageDto> messages = privateChatService.getChatMessagesPaginated(principal.getName(), otherUser, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages/{sender}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markMessagesAsRead(
            Principal principal,
            @PathVariable String sender) {
        log.debug("Marking messages as read from {} to {}", sender, principal.getName());
        privateChatService.markMessagesAsRead(principal.getName(), sender);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadMessageCount(Principal principal) {
        long count = privateChatService.getUnreadMessageCount(principal.getName());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/unread/count/{sender}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadMessageCountFromSender(
            Principal principal,
            @PathVariable String sender) {
        long count = privateChatService.getUnreadMessageCountFromUser(principal.getName(), sender);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/partners")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getChatPartners(Principal principal) {
        List<String> partners = privateChatService.getChatPartners(principal.getName());
        return ResponseEntity.ok(partners);
    }

    @GetMapping("/online/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isUserOnline(@PathVariable String username) {
        boolean isOnline = privateChatService.isUserOnline(username);
        return ResponseEntity.ok(isOnline);
    }

    @GetMapping("/room-id/{otherUser}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getChatRoomId(
            Principal principal,
            @PathVariable String otherUser) {
        String roomId = privateChatService.generateChatRoomId(principal.getName(), otherUser);
        return ResponseEntity.ok(roomId);
    }

     @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> searchUsers(
            Principal principal,
            @RequestParam String query) {
        log.debug("Searching users with query: {}", query);
        // Временная реализация - возвращаем пустой список
        // В реальном приложении здесь будет поиск по базе данных
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> startChat(
            Principal principal,
            @RequestBody Map<String, String> request) {
        String username = request.get("username");
        log.debug("Starting chat between {} and {}", principal.getName(), username);
        
        // Временная реализация - просто возвращаем roomId
        String roomId = privateChatService.generateChatRoomId(principal.getName(), username);
        return ResponseEntity.ok(Map.of("roomId", roomId, "status", "started"));
    }

    @GetMapping("/online-users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getOnlineUsers() {
        Set<String> onlineUsers = privateChatService.getOnlineUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("users", onlineUsers);
        response.put("count", onlineUsers.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadAttachment(
            @RequestParam("file") MultipartFile file) {
        log.debug("Uploading file: {}", file.getOriginalFilename());
        
        // Временная реализация - возвращаем фиктивный URL
        // В реальном приложении здесь будет загрузка файла в облачное хранилище
        String fileUrl = "/uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}