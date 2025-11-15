package medicalcenter.userservice.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log4j2
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file, UUID doctorId) throws IOException {
        try {
            Path uploadPath = Paths.get(uploadDir, "avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String fileName = "doctor-" + doctorId + fileExtension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            return "/avatars/" + fileName;
        } catch (IOException e) {
            log.error("Could not store file for doctor {}", doctorId, e);
            throw new IOException("Could not store file: " + file.getOriginalFilename(), e);
        }
    }

    public boolean deleteFile(String filePath) {
        try {
            if (filePath != null && !filePath.isEmpty()) {
                Path path = Paths.get(uploadDir, filePath);
                return Files.deleteIfExists(path);
            }
            return false;
        } catch (IOException e) {
            log.error("Could not delete file: {}", filePath, e);
            return false;
        }
    }

    public byte[] loadFile(String filePath) throws IOException {
        try {
            if (filePath != null && !filePath.isEmpty()) {
                Path path = Paths.get(uploadDir, filePath);
                return Files.readAllBytes(path);
            }
            return null;
        } catch (IOException e) {
            log.error("Could not load file: {}", filePath, e);
            throw new IOException("Could not load file: " + filePath, e);
        }
    }
}