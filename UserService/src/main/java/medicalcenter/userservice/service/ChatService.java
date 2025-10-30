package medicalcenter.userservice.service;

import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.MessageType;
import medicalcenter.userservice.model.dto.message.ChatMessageDto;
import medicalcenter.userservice.model.entity.ChatMessage;
import medicalcenter.userservice.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository repository;

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public void registerOnline(String username) {
        onlineUsers.add(username);
        broadcastSystem(String.format("%s is online", username));
        broadcastOnlineUsers();
    }

    public void registerOffline(String username) {
        onlineUsers.remove(username);
        broadcastSystem(String.format("%s is offline", username));
        broadcastOnlineUsers();
    }

    public void handleIncomingMessage(ChatMessageDto dto, Principal principal) {
        String senderName = principal != null ? principal.getName() : dto.senderName();
        UUID senderId = dto.senderId();

        ChatMessage m = new ChatMessage();
        m.setSenderId(senderId);
        m.setSenderName(senderName);
        m.setContent(dto.content());
        m.setAttachmentUrl(dto.attachmentUrl());
        m.setTimestamp(LocalDateTime.now());
        m.setType(dto.type() == null ? MessageType.USER : dto.type());

        ChatMessage saved = repository.save(m);

        ChatMessageDto out = toDto(saved);
        messagingTemplate.convertAndSend("/topic/support", out);
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
        Map<String,Object> payload = new HashMap<>();
        payload.put("type", "ONLINE_USERS");
        payload.put("users", getOnlineUsers());
        messagingTemplate.convertAndSend("/topic/support", payload);
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
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
