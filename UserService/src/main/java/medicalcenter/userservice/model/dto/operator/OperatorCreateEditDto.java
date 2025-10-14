package medicalcenter.userservice.model.dto.operator;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record OperatorCreateEditDto(
        @NotEmpty(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @NotEmpty(message = "Last name should not be empty")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
        String middleName,

        @NotNull(message = "Date of birth should not be null")
        @Past(message = "Date of birth should be in the past")
        LocalDate dateOfBirth,

        @NotEmpty(message = "Phone should not be empty")
        @Pattern(regexp = "^80(29|17|33|44|25)\\d{7}$", message = "Phone must have format 80(29|17|33|44|25) followed by 7 digits")
        String phone,

        @Email(message = "Email should be valid")
        String email
) {
}