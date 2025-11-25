package medicalcenter.userservice.model.dto.privatechat;

public record PrivateTypingEvent(
        String username,
        String chatRoomId,
        Boolean isTyping
) {}