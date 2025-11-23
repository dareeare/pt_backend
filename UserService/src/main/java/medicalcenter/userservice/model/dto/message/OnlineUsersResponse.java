package medicalcenter.userservice.model.dto.message;

import java.util.Set;

/**
 * DTO для ответа со списком онлайн пользователей
 */
public record OnlineUsersResponse(
        Set<String> onlineUsers,
        Integer count
) {}

