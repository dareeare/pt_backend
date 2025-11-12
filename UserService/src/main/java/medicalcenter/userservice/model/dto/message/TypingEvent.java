package medicalcenter.userservice.model.dto.message;

/**
 * DTO для события набора текста (typing indicator)
 */
public record TypingEvent(
        String username,
        Boolean isTyping
) {}

