package medicalcenter.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.PrivateMessageType;
import medicalcenter.userservice.model.dto.privatechat.PrivateChatMessageDto;
import medicalcenter.userservice.model.dto.privatechat.PrivateSendMessageRequest;
import medicalcenter.userservice.model.dto.privatechat.PrivateTypingEvent;
import medicalcenter.userservice.model.entity.PrivateChatMessage;
import medicalcenter.userservice.repository.PrivateChatMessageRepository;
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.OperatorRepository;
import medicalcenter.userservice.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OperatorRepository operatorRepository;

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    private final Map<String, Map<String, LocalDateTime>> typingUsers = new ConcurrentHashMap<>();

    // Helper record to hold resolved user info
    private record UserInfo(String name, String avatarUrl) {}

    public void registerOnline(String username) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Попытка регистрации онлайн от неаутентифицированного пользователя");
            return;
        }

        onlineUsers.add(username);
        log.info("🟢 User {} is now online for private chat. Total online: {}", username, onlineUsers.size());
        broadcastOnlineUsers();
    }

    public void registerOffline(String username) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Попытка регистрации оффлайн от неаутентифицированного пользователя");
            return;
        }

        onlineUsers.remove(username);
        typingUsers.remove(username);
        log.info("🔴 User {} is now offline from private chat. Total online: {}", username, onlineUsers.size());
        broadcastOnlineUsers();
    }

    public String generateChatRoomId(String user1, String user2) {
        List<String> users = Arrays.asList(user1, user2);
        Collections.sort(users);
        return "private_" + String.join("_", users);
    }

    @Transactional
    public PrivateChatMessageDto sendPrivateMessage(PrivateSendMessageRequest request) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("❌ Неаутентифицированный пользователь пытается отправить сообщение");
            throw new SecurityException("Authentication required");
        }

        String currentUser = auth.getName();
        String recipient = request.recipient();
        
        if (currentUser.equals(recipient)) {
            throw new IllegalArgumentException("Cannot send message to yourself");
        }

        UserInfo userInfo = resolveUserInfoByPhone(currentUser);
        
        // Clear typing indicator
        clearTypingIndicator(currentUser, generateChatRoomId(currentUser, recipient));

        PrivateChatMessage message = PrivateChatMessage.builder()
                .senderName(userInfo.name())
                .senderAvatarUrl(userInfo.avatarUrl())
                .recipient(recipient)
                .chatRoomId(generateChatRoomId(currentUser, recipient))
                .content(request.content())
                .timestamp(LocalDateTime.now())
                .type(PrivateMessageType.USER)
                .attachmentUrl(request.attachmentUrl())
                .read(false)
                .build();

        PrivateChatMessage saved = privateChatMessageRepository.save(message);
        log.debug("✅ Private message saved: id={}, from={}, to={}", saved.getId(), currentUser, recipient);

        PrivateChatMessageDto dto = toDto(saved);
        
        // Send to both users via their specific channels
        messagingTemplate.convertAndSendToUser(currentUser, "/queue/private/messages", dto);
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private/messages", dto);
        
        // Also send to chat room for real-time updates
        messagingTemplate.convertAndSend("/topic/private/" + saved.getChatRoomId(), dto);

        return dto;
    }

    @Transactional
    public void markMessagesAsRead(String sender) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("❌ Неаутентифицированный пользователь пытается отметить сообщения как прочитанные");
            return;
        }

        String currentUser = auth.getName();
        privateChatMessageRepository.markMessagesAsRead(currentUser, sender);
        log.debug("✅ Marked messages as read from {} to {}", sender, currentUser);
        
        // Notify sender that messages were read
        String chatRoomId = generateChatRoomId(currentUser, sender);
        Map<String, Object> readReceipt = new HashMap<>();
        readReceipt.put("type", "MESSAGES_READ");
        readReceipt.put("reader", currentUser);
        readReceipt.put("chatRoomId", chatRoomId);
        messagingTemplate.convertAndSend("/topic/private/" + chatRoomId, readReceipt);
    }

    @Transactional(readOnly = true)
    public long getUnreadMessageCount() {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Неаутентифицированный пользователь пытается получить количество непрочитанных сообщений");
            return 0;
        }

        String username = auth.getName();
        return privateChatMessageRepository.countUnreadMessages(username);
    }

    @Transactional(readOnly = true)
    public long getUnreadMessageCountFromUser(String sender) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Неаутентифицированный пользователь пытается получить количество непрочитанных сообщений");
            return 0;
        }

        String username = auth.getName();
        return privateChatMessageRepository.countUnreadMessagesFromSender(username, sender);
    }

    @Transactional(readOnly = true)
    public List<String> getChatPartners() {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Неаутентифицированный пользователь пытается получить список чат-партнеров");
            return List.of();
        }

        String username = auth.getName();
        return privateChatMessageRepository.findChatPartners(username);
    }

    public void handlePrivateTypingEvent(PrivateTypingEvent event) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Неаутентифицированный пользователь пытается отправить событие набора текста");
            return;
        }

        String username = auth.getName();
        String chatRoomId = event.chatRoomId();
        boolean isTyping = event.isTyping();

        if (isTyping) {
            typingUsers.computeIfAbsent(username, k -> new ConcurrentHashMap<>())
                      .put(chatRoomId, LocalDateTime.now());
        } else {
            Map<String, LocalDateTime> userTyping = typingUsers.get(username);
            if (userTyping != null) {
                userTyping.remove(chatRoomId);
                if (userTyping.isEmpty()) {
                    typingUsers.remove(username);
                }
            }
        }

        // Broadcast typing event to chat room
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "TYPING");
        payload.put("username", username);
        payload.put("chatRoomId", chatRoomId);
        payload.put("isTyping", isTyping);
        
        messagingTemplate.convertAndSend("/topic/private/" + chatRoomId, payload);
    }

    private void clearTypingIndicator(String username, String chatRoomId) {
        Map<String, LocalDateTime> userTyping = typingUsers.get(username);
        if (userTyping != null) {
            userTyping.remove(chatRoomId);
            if (userTyping.isEmpty()) {
                typingUsers.remove(username);
            }
        }
    }

    public void joinPrivateChatRoom(String chatRoomId) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Неаутентифицированный пользователь пытается присоединиться к чату");
            return;
        }

        String username = auth.getName();
        messagingTemplate.convertAndSend("/topic/private/" + chatRoomId, 
            createSystemMessage(username + " joined the chat", chatRoomId));
        log.debug("✅ User {} joined private chat room: {}", username, chatRoomId);
    }

    public void leavePrivateChatRoom(String chatRoomId) {
        // Проверяем аутентификацию вручную
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(auth)) {
            log.warn("⚠️ Неаутентифицированный пользователь пытается покинуть чат");
            return;
        }

        String username = auth.getName();
        messagingTemplate.convertAndSend("/topic/private/" + chatRoomId,
            createSystemMessage(username + " left the chat", chatRoomId));
        log.debug("✅ User {} left private chat room: {}", username, chatRoomId);
    }

    private PrivateChatMessageDto createSystemMessage(String content, String chatRoomId) {
        return PrivateChatMessageDto.builder()
                .id(UUID.randomUUID())
                .senderName("System")
                .content(content)
                .timestamp(LocalDateTime.now())
                .type(PrivateMessageType.SYSTEM)
                .chatRoomId(chatRoomId)
                .read(true)
                .build();
    }

    private void broadcastOnlineUsers() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "ONLINE_USERS");
        payload.put("users", getOnlineUsers());
        payload.put("count", onlineUsers.size());
        messagingTemplate.convertAndSend("/topic/private/online", payload);
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }

    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }

    // Утилитный метод для проверки аутентификации
    private boolean isAuthenticated(Authentication auth) {
        return auth != null && 
               auth.isAuthenticated() && 
               !"anonymous".equals(auth.getName());
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

    private PrivateChatMessageDto toDto(PrivateChatMessage entity) {
        return PrivateChatMessageDto.builder()
                .id(entity.getId())
                .senderName(entity.getSenderName())
                .senderAvatarUrl(entity.getSenderAvatarUrl())
                .recipient(entity.getRecipient())
                .chatRoomId(entity.getChatRoomId())
                .content(entity.getContent())
                .timestamp(entity.getTimestamp())
                .type(entity.getType())
                .attachmentUrl(entity.getAttachmentUrl())
                .read(entity.isRead())
                .build();
    }

    @Transactional(readOnly = true)
public List<PrivateChatMessageDto> getChatMessages(String otherUser) {
    // Разрешаем анонимный доступ к чтению чатов
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = auth != null ? auth.getName() : "anonymous";
    
    log.debug("User {} accessing chat with {}", currentUser, otherUser);
    
    List<PrivateChatMessage> messages = privateChatMessageRepository.findChatBetweenUsers(currentUser, otherUser);
    return messages.stream()
            .map(this::toDto)
            .toList();
}

@Transactional(readOnly = true)
public Page<PrivateChatMessageDto> getChatMessagesPaginated(String otherUser, int page, int size) {
    // Разрешаем анонимный доступ к чтению чатов
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String currentUser = auth != null ? auth.getName() : "anonymous";
    
    log.debug("User {} accessing paginated chat with {}", currentUser, otherUser);
    
    String chatRoomId = generateChatRoomId(currentUser, otherUser);
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
    Page<PrivateChatMessage> messages = privateChatMessageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageable);
    return messages.map(this::toDto);
}
}