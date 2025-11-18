package medicalcenter.userservice.model.dto.logging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientLogDto {
    @NotNull
    private ClientLogLevel level;

    @NotBlank
    private String message;

    private String category;

    private String stack;

    @Builder.Default
    private Map<String, Object> context = new HashMap<>();

    private Instant timestamp;

    private String url;

    private String userAgent;

    private String sessionId;

    private String userId;
}

