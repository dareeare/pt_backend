package medicalcenter.userservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientCreateEditDto(
        @NotEmpty(message = "First name should not be empty")
        @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
        String firstName,

        @NotEmpty(message = "Last name should not be empty")
        @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
        String lastName,

        @Size(min = 2, max = 50, message = "Middle name should be between 2 and 50 characters")
        String middleName,

        @NotEmpty(message = "Phone should not be empty")
        String phone,

        @Email
        String email,

        @Past(message = "Date of birth should be in the past")
        LocalDate dateOfBirth,

        @NotEmpty(message = "Gender should not be empty")
        @Pattern(regexp = "^[MFO]$", message = "Gender must be one of 'M', 'F', or 'O'")
        char gender
) {
}
