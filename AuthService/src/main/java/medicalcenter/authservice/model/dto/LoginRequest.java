package medicalcenter.authservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @Pattern(regexp = "^80(29|17|33|44|25)\\d{7}$") String phone,
        @NotBlank String password
) {
}
