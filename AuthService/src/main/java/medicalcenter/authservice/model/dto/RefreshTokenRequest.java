package medicalcenter.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank String token) {
}
