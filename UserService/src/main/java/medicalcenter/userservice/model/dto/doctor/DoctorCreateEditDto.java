package medicalcenter.userservice.model.dto.doctor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DoctorCreateEditDto(
        @NotEmpty(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @NotEmpty(message = "Last name should not be empty")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
        String middleName,

        @NotEmpty(message = "Specialty should not be empty")
        @Size(min = 2, max = 100, message = "Specialty should be between 2 and 100 characters")
        String specialty,

        @NotEmpty(message = "Phone should not be empty")
        @Pattern(regexp = "^\\d{11}$", message = "Phone must contain 11 digits")
        String phone,

        @Email(message = "Email should be valid")
        String email,

        String information,

        @DecimalMin(value = "1.00", message = "Rating must be at least 1.00")
        @DecimalMax(value = "5.00", message = "Rating must be at most 5.00")
        BigDecimal rating
) {
}
