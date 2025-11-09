package medicalcenter.userservice.model.dto.message;

import medicalcenter.userservice.model.MessageType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для поиска сообщений с фильтрами
 */
public record MessageSearchRequest(
        String query,
        UUID senderId,
        MessageType type,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime fromDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime toDate,
        Integer page,
        Integer size
) {
    public MessageSearchRequest {
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = 50;
        }
        if (size > 100) {
            size = 100;
        }
    }
}

