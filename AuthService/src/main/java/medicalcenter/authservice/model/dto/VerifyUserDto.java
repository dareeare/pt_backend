package medicalcenter.authservice.model.dto;

public record VerifyUserDto(
        String phone,
        String verificationCode
) {
}
