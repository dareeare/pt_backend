package medicalcenter.userservice.controller;

import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.message.ChatMessageDto;
import medicalcenter.userservice.model.entity.ChatMessage;
import medicalcenter.userservice.repository.ChatMessageRepository;
import medicalcenter.userservice.service.ChatService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatMessageRepository repository;
    private final ChatService chatService;
    private final Path uploadDir = Paths.get("uploads/chat");

    @GetMapping("/messages")
    public List<ChatMessageDto> getRecentMessages(@RequestParam(defaultValue = "50") int size) {
        List<ChatMessage> list = repository.findAllByOrderByTimestampDesc(PageRequest.of(0, size));
        return list.stream()
                .map(m -> ChatMessageDto.builder()
                        .id(m.getId())
                        .senderId(m.getSenderId())
                        .senderName(m.getSenderName())
                        .content(m.getContent())
                        .timestamp(m.getTimestamp())
                        .type(m.getType())
                        .attachmentUrl(m.getAttachmentUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendFromServer(@RequestBody ChatMessageDto dto, Principal principal) {
        chatService.handleIncomingMessage(dto, principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("empty file");
        try {
            String filename = System.currentTimeMillis() + "_" + Path.of(Objects.requireNonNull(file.getOriginalFilename())).getFileName().toString();
            Path target = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            String url = "/uploads/chat/" + filename;
            return ResponseEntity.ok().body(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("upload error");
        }
    }
}
