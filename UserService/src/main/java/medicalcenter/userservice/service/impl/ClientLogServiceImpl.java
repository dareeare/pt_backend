package medicalcenter.userservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.logging.ClientLogDto;
import medicalcenter.userservice.model.dto.logging.ClientLogLevel;
import medicalcenter.userservice.service.ClientLogService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "medicalcenter.userservice.clientlog")
public class ClientLogServiceImpl implements ClientLogService {

    private static final int MAX_STACK_LENGTH = 4000;

    private final ObjectMapper objectMapper;

    @Override
    public void persist(ClientLogDto dto, String sourceIp) {
        ClientLogLevel level = Optional.ofNullable(dto.getLevel()).orElse(ClientLogLevel.INFO);
        Map<String, Object> payload = new LinkedHashMap<>();

        payload.put("timestamp", Optional.ofNullable(dto.getTimestamp()).orElse(Instant.now()));
        payload.put("sourceIp", sourceIp);
        payload.put("level", level);
        payload.put("message", dto.getMessage());
        payload.put("category", dto.getCategory());
        payload.put("url", dto.getUrl());
        payload.put("userAgent", dto.getUserAgent());
        payload.put("sessionId", dto.getSessionId());
        payload.put("userId", dto.getUserId());
        payload.put("context", dto.getContext());
        payload.put("stack", truncate(dto.getStack()));

        write(level, payload);
    }

    private void write(ClientLogLevel level, Map<String, Object> payload) {
        String serialized = serialize(payload);
        switch (level) {
            case ERROR -> log.error(serialized);
            case WARN -> log.warn(serialized);
            case DEBUG -> log.debug(serialized);
            default -> log.info(serialized);
        }
    }

    private String serialize(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return payload.toString();
        }
    }

    private String truncate(String stack) {
        if (stack == null) {
            return null;
        }
        return stack.length() > MAX_STACK_LENGTH ? stack.substring(0, MAX_STACK_LENGTH) : stack;
    }
}


