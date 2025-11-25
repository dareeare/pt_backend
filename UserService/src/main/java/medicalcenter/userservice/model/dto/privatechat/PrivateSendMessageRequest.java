package medicalcenter.userservice.model.dto.privatechat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PrivateSendMessageRequest(
        @NotBlank(message = "Содержание сообщения не может быть пустым")
        @Size(max = 5000, message = "Сообщение не может быть длиннее 5000 символов")
        String content,
        
        String recipient,
        String attachmentUrl
) {}