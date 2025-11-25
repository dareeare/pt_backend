package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.privatechat.PrivateSendMessageRequest;
import medicalcenter.userservice.model.dto.privatechat.PrivateTypingEvent;
import medicalcenter.userservice.service.PrivateChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PrivateChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateChatService privateChatService;

    // УБРАЛИ @PreAuthorize - проверяем вручную в сервисе
    @MessageMapping("/private/user.online")
    @SendTo("/topic/private/online")
    public Map<String, Object> userOnline() {
        // Проверка аутентификации теперь в сервисе
        privateChatService.registerOnline(getCurrentUsername());
        
        return Map.of(
            "status", "online",
            "timestamp", System.currentTimeMillis()
        );
    }

    @MessageMapping("/private/user.offline")
    @SendTo("/topic/private/online")
    public Map<String, Object> userOffline() {
        // Проверка аутентификации теперь в сервисе
        privateChatService.registerOffline(getCurrentUsername());
        
        return Map.of(
            "status", "offline", 
            "timestamp", System.currentTimeMillis()
        );
    }

    @MessageMapping("/private/message.send")
    public void sendPrivateMessage(@Payload PrivateSendMessageRequest request) {
        // Проверка аутентификации теперь в сервисе
        privateChatService.sendPrivateMessage(request);
    }

    @MessageMapping("/private/typing")
    public void handleTyping(@Payload PrivateTypingEvent event) {
        // Проверка аутентификации теперь в сервисе
        privateChatService.handlePrivateTypingEvent(event);
    }

    @MessageMapping("/private/messages.read")
    public void markMessagesAsRead(@Payload Map<String, String> payload) {
        String sender = payload.get("sender");
        // Проверка аутентификации теперь в сервисе
        privateChatService.markMessagesAsRead(sender);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}