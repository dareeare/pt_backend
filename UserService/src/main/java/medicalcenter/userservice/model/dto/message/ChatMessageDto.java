package medicalcenter.userservice.model.dto.message;

import lombok.Builder;
import medicalcenter.userservice.model.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ChatMessageDto(
        UUID id,
    UUID senderId,
    String senderName,
    String senderAvatarUrl,
    String content,
    LocalDateTime timestamp,
    MessageType type,
    String attachmentUrl) {}
