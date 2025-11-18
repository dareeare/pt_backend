package medicalcenter.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "DTO для загрузки аватарки")
public record AvatarUploadDto(
        @Schema(description = "Файл аватарки", requiredMode = Schema.RequiredMode.REQUIRED)
        MultipartFile avatarFile
) {
}