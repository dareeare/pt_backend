package medicalcenter.authservice.model.dto;

public record VerifyUserDto(
        String email,
        String verificationCode
) {
}
