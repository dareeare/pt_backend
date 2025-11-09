package medicalcenter.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.MessageType;
import medicalcenter.userservice.model.dto.message.ChatMessageDto;
import medicalcenter.userservice.model.dto.message.OnlineUsersResponse;
import medicalcenter.userservice.model.dto.message.SendMessageRequest;
import medicalcenter.userservice.service.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * REST контроллер для управления чатом технической поддержки
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Support", description = "API для чата технической поддержки")
public class ChatRestController {
    private final ChatService chatService;
    private final Path uploadDir = Paths.get("uploads/chat");

    @Operation(summary = "Получить последние сообщения", description = "Возвращает последние сообщения с пагинацией")
    @GetMapping("/messages")
    public ResponseEntity<Page<ChatMessageDto>> getRecentMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        if (size > 100) {
            size = 100;
        }
        if (size < 1) {
            size = 50;
        }
        Page<ChatMessageDto> messages = chatService.getRecentMessages(page, size);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Поиск сообщений", description = "Поиск сообщений с фильтрами по содержимому, отправителю, типу и дате")
    @GetMapping("/messages/search")
    public ResponseEntity<Page<ChatMessageDto>> searchMessages(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID senderId,
            @RequestParam(required = false) MessageType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        if (size > 100) {
            size = 100;
        }
        if (size < 1) {
            size = 50;
        }
        Page<ChatMessageDto> messages = chatService.searchMessages(
                query, senderId, type, fromDate, toDate, page, size
        );
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Отправить сообщение", description = "Отправка нового сообщения в чат поддержки")
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @RequestBody @Valid SendMessageRequest request,
            Principal principal,
            @RequestParam(required = false) UUID senderId,
            @RequestParam(required = false) MessageType messageType
    ) {
        UUID userId = senderId;
        if (userId == null && principal != null) {
            // Здесь можно получить userId из principal, если он содержит информацию о пользователе
            // Пока используем null, так как структура principal не ясна
        }
        
        MessageType type = messageType != null ? messageType : MessageType.USER;
        ChatMessageDto message = chatService.handleIncomingMessage(request, principal, userId, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Operation(summary = "Получить список онлайн пользователей", description = "Возвращает список пользователей, которые сейчас онлайн")
    @GetMapping("/online-users")
    public ResponseEntity<OnlineUsersResponse> getOnlineUsers() {
        var onlineUsers = chatService.getOnlineUsers();
        OnlineUsersResponse response = new OnlineUsersResponse(onlineUsers, onlineUsers.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Загрузить файл", description = "Загрузка файла для прикрепления к сообщению")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("Файл не может быть пустым"));
        }

        try {
            // Создаем директорию, если её нет
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Генерируем уникальное имя файла
            String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
            String filename = System.currentTimeMillis() + "_" + Path.of(originalFilename).getFileName().toString();
            
            // Очищаем имя файла от небезопасных символов
            filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
            
            Path target = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            
            String url = "/uploads/chat/" + filename;
            log.info("File uploaded: {}", url);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            response.put("filename", filename);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Ошибка при загрузке файла: " + e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
