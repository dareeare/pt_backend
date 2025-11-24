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
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.OperatorRepository;
import medicalcenter.userservice.repository.PatientRepository;
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
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OperatorRepository operatorRepository;

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    private final Map<String, LocalDateTime> typingUsers = new ConcurrentHashMap<>();

    // Helper record to hold resolved user info
    private record UserInfo(String name, String avatarUrl) {}

    public void registerOnline(String username) {
        onlineUsers.add(username);
        log.info("User {} is now online. Total online: {}", username, onlineUsers.size());
        broadcastOnlineUsers();
    }

    public void registerOffline(String username) {
        onlineUsers.remove(username);
        typingUsers.remove(username);
        log.info("User {} is now offline. Total online: {}", username, onlineUsers.size());
        broadcastOnlineUsers();
    }

    @Transactional
    public ChatMessageDto handleIncomingMessage(SendMessageRequest request, Principal principal, UUID senderId, MessageType messageType) {
        String phone = principal != null ? principal.getName() : "Anonymous";
        String senderName = phone;
        String senderAvatarUrl = null;

        if (principal != null) {
             UserInfo userInfo = resolveUserInfoByPhone(phone);
             senderName = userInfo.name();
             senderAvatarUrl = userInfo.avatarUrl();
        }

        if (phone != null) {
            typingUsers.remove(phone);
            broadcastTypingStatus(phone, false);
        }

        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setSenderAvatarUrl(senderAvatarUrl);
        message.setContent(request.content());
        message.setAttachmentUrl(request.attachmentUrl());
        message.setTimestamp(LocalDateTime.now());
        message.setType(messageType != null ? messageType : MessageType.USER);

        ChatMessage saved = repository.save(message);
        log.debug("Message saved: id={}, sender={}, content={}", saved.getId(), senderName, saved.getContent());

        ChatMessageDto dto = toDto(saved);
        messagingTemplate.convertAndSend("/topic/support", dto);
        return dto;
    }

    private UserInfo resolveUserInfoByPhone(String phone) {
        var op = operatorRepository.findByPhone(phone);
        if (op.isPresent()) {
            return new UserInfo(
                op.get().getFirstName() + " " + op.get().getLastName(), 
                op.get().getAvatarPath()
            );
        }
        var doc = doctorRepository.findByPhone(phone);
        if (doc.isPresent()) {
            return new UserInfo(
                doc.get().getFirstName() + " " + doc.get().getLastName(), 
                doc.get().getAvatarPath()
            );
        }
        var pat = patientRepository.findByPhone(phone);
        if (pat.isPresent()) {
            return new UserInfo(
                pat.get().getFirstName() + " " + pat.get().getLastName(), 
                pat.get().getAvatarPath()
            );
        }
        return new UserInfo(phone, null);
    }

    @Transactional
    public ChatMessageDto handleIncomingMessage(ChatMessageDto dto, Principal principal) {
        String phone = principal != null ? principal.getName() : dto.senderName();
        UUID senderId = dto.senderId();
        String senderName = phone;
        String senderAvatarUrl = null;

        if (principal != null) {
            UserInfo userInfo = resolveUserInfoByPhone(phone);
            senderName = userInfo.name();
            senderAvatarUrl = userInfo.avatarUrl();
        }

        if (senderName.equals(phone) && !"Anonymous".equals(phone)) {
            // User not found in DB
        }

        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setSenderAvatarUrl(senderAvatarUrl);
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
        } else {
            typingUsers.remove(username);
            broadcastTypingStatus(username, false);
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
        log.debug("System message: {}", content);
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ChatMessage> messages = repository.findAllByOrderByTimestampDesc(pageable);
        return messages.map(this::toDto);
    }

    public Page<ChatMessageDto> getOperatorMessages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Specification<ChatMessage> spec = ChatMessageSpecifications.searchMessages(null, null, null, null, null)
                .and((root, query, cb) -> 
                    cb.or(
                        cb.equal(root.get("type"), MessageType.OPERATOR),
                        cb.equal(root.get("type"), MessageType.SYSTEM)
                    )
                );
        Page<ChatMessage> messages = repository.findAll(spec, pageable);
        return messages.map(this::toDto);
    }

    private ChatMessageDto toDto(ChatMessage entity) {
        return ChatMessageDto.builder()
                .id(entity.getId())
                .senderId(entity.getSenderId())
                .senderName(entity.getSenderName())
                .senderAvatarUrl(entity.getSenderAvatarUrl())
                .content(entity.getContent())
                .timestamp(entity.getTimestamp())
                .type(entity.getType())
                .attachmentUrl(entity.getAttachmentUrl())
                .build();
    }
}
