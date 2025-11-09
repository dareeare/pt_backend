package medicalcenter.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.MessageType;
import medicalcenter.userservice.model.dto.message.ChatMessageDto;
import medicalcenter.userservice.model.dto.message.SendMessageRequest;
import medicalcenter.userservice.model.dto.message.TypingEvent;
import medicalcenter.userservice.model.entity.ChatMessage;
import medicalcenter.userservice.repository.ChatMessageRepository;
import medicalcenter.userservice.repository.ChatMessageSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository repository;

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    private final Map<String, LocalDateTime> typingUsers = new ConcurrentHashMap<>();

    public void registerOnline(String username) {
        onlineUsers.add(username);
        log.info("User {} is now online. Total online: {}", username, onlineUsers.size());
        broadcastSystem(String.format("Пользователь %s подключился к чату", username));
        broadcastOnlineUsers();
    }

    public void registerOffline(String username) {
        onlineUsers.remove(username);
        typingUsers.remove(username);
        log.info("User {} is now offline. Total online: {}", username, onlineUsers.size());
        broadcastSystem(String.format("Пользователь %s отключился от чата", username));
        broadcastOnlineUsers();
    }

    @Transactional
    public ChatMessageDto handleIncomingMessage(SendMessageRequest request, Principal principal, UUID senderId, MessageType messageType) {
        String senderName = principal != null ? principal.getName() : "Anonymous";
        
        // Останавливаем typing indicator при отправке сообщения
        if (senderName != null) {
            typingUsers.remove(senderName);
            broadcastTypingStatus(senderName, false);
        }

        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setContent(request.content());
        message.setAttachmentUrl(request.attachmentUrl());
        message.setTimestamp(LocalDateTime.now());
        message.setType(messageType != null ? messageType : MessageType.USER);

        ChatMessage saved = repository.save(message);
        log.debug("Message saved: id={}, sender={}, content={}", saved.getId(), senderName, saved.getContent().substring(0, Math.min(50, saved.getContent().length())));

        ChatMessageDto dto = toDto(saved);
        // Рассылка всем онлайн пользователям
        messagingTemplate.convertAndSend("/topic/support", dto);
        return dto;
    }

    @Transactional
    public ChatMessageDto handleIncomingMessage(ChatMessageDto dto, Principal principal) {
        String senderName = principal != null ? principal.getName() : dto.senderName();
        UUID senderId = dto.senderId();

        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setContent(dto.content());
        message.setAttachmentUrl(dto.attachmentUrl());
        message.setTimestamp(LocalDateTime.now());
        message.setType(dto.type() == null ? MessageType.USER : dto.type());

        ChatMessage saved = repository.save(message);

        ChatMessageDto out = toDto(saved);
        messagingTemplate.convertAndSend("/topic/support", out);
        return out;
    }

    public void handleTypingEvent(String username, boolean isTyping) {
        if (isTyping) {
            typingUsers.put(username, LocalDateTime.now());
            broadcastTypingStatus(username, true);
            log.debug("User {} started typing", username);
        } else {
            typingUsers.remove(username);
            broadcastTypingStatus(username, false);
            log.debug("User {} stopped typing", username);
        }
    }

    private void broadcastTypingStatus(String username, boolean isTyping) {
        TypingEvent event = new TypingEvent(username, isTyping);
        messagingTemplate.convertAndSend("/topic/support", createTypingPayload(event));
    }

    private Map<String, Object> createTypingPayload(TypingEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "TYPING");
        payload.put("username", event.username());
        payload.put("isTyping", event.isTyping());
        return payload;
    }

    public void broadcastSystem(String content) {
        ChatMessage sys = new ChatMessage();
        sys.setContent(content);
        sys.setSenderName("system");
        sys.setTimestamp(LocalDateTime.now());
        sys.setType(MessageType.SYSTEM);
        repository.save(sys);
        messagingTemplate.convertAndSend("/topic/support", toDto(sys));
    }

    public void broadcastOnlineUsers() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ONLINE_USERS");
        payload.put("users", getOnlineUsers());
        payload.put("count", onlineUsers.size());
        messagingTemplate.convertAndSend("/topic/support", payload);
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }

    public Page<ChatMessageDto> searchMessages(
            String query,
            UUID senderId,
            MessageType type,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Specification<ChatMessage> spec = ChatMessageSpecifications.searchMessages(query, senderId, type, fromDate, toDate);
        Page<ChatMessage> messages = repository.findAll(spec, pageable);
        return messages.map(this::toDto);
    }

    public Page<ChatMessageDto> getRecentMessages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = repository.findAllByOrderByTimestampDesc(pageable);
        return messages.map(this::toDto);
    }

    private ChatMessageDto toDto(ChatMessage entity) {
        return ChatMessageDto.builder()
                .id(entity.getId())
                .senderId(entity.getSenderId())
                .senderName(entity.getSenderName())
                .content(entity.getContent())
                .timestamp(entity.getTimestamp())
                .type(entity.getType())
                .attachmentUrl(entity.getAttachmentUrl())
                .build();
    }
}
