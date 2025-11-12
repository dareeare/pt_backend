package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.message.ChatMessageDto;
import medicalcenter.userservice.model.dto.message.TypingEvent;
import medicalcenter.userservice.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * WebSocket контроллер для обработки сообщений чата в реальном времени
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    private final ChatService chatService;

    /**
     * Обработчик входящих сообщений через WebSocket
     * Эндпоинт: /app/support
     */
    @MessageMapping("/support")
    public void onMessage(@Payload ChatMessageDto message, Principal principal) {
        log.debug("Received message from {}: {}", principal != null ? principal.getName() : "anonymous", message.content());
        chatService.handleIncomingMessage(message, principal);
    }

    /**
     * Обработчик событий набора текста (typing indicator)
     * Эндпоинт: /app/typing
     */
    @MessageMapping("/typing")
    public void onTyping(@Payload TypingEvent typingEvent, Principal principal) {
        String username = principal != null ? principal.getName() : typingEvent.username();
        if (username != null) {
            log.debug("User {} typing: {}", username, typingEvent.isTyping());
            chatService.handleTypingEvent(username, typingEvent.isTyping());
        }
    }
}
