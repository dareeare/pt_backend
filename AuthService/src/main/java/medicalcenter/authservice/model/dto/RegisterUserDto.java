package medicalcenter.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record RegisterUserDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Pattern(regexp = "^80(29|17|33|44|25)\\d{7}$") String phone,
        @Email String email,
        @Past LocalDate birthDate,
        @NotBlank String password,
        @NotBlank String avatarUrl,
        // Optional fields for specific roles
        String specialty,
        String gender
) {}
