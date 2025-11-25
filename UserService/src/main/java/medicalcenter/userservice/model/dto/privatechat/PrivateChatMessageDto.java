package medicalcenter.userservice.model.dto.privatechat;

import lombok.Builder;
import medicalcenter.userservice.model.PrivateMessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PrivateChatMessageDto(
        UUID id,
        String senderName,
        String senderAvatarUrl,
        String recipient,
        String chatRoomId,
        String content,
        LocalDateTime timestamp,
        PrivateMessageType type,
        String attachmentUrl,
        boolean read
) {}