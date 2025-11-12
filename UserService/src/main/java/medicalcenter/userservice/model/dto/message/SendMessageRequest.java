package medicalcenter.userservice.model.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для отправки сообщения в чат поддержки
 */
public record SendMessageRequest(
        @NotBlank(message = "Содержание сообщения не может быть пустым")
        @Size(max = 5000, message = "Сообщение не может быть длиннее 5000 символов")
        String content,
        
        String attachmentUrl
) {}

