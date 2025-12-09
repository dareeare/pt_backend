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

    public String storeDoctorFile(MultipartFile file, UUID doctorId) throws IOException {
        return storeFile(file, doctorId, "doctors", "doctor");
    }

    public String storePatientFile(MultipartFile file, UUID patientId) throws IOException {
        return storeFile(file, patientId, "patients", "patient");
    }

    public String storeOperatorFile(MultipartFile file, UUID operatorId) throws IOException {
        return storeFile(file, operatorId, "operators", "operator");
    }

    public String storeManagerFile(MultipartFile file, UUID managerId) throws IOException {
        return storeFile(file, managerId, "managers", "manager");
    }

    private String storeFile(MultipartFile file, UUID entityId, String entityType, String prefix) throws IOException {
        try {
            Path uploadPath = Paths.get(uploadDir, "avatars", entityType);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);

            String fileName = prefix + "-" + entityId + fileExtension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            return "/avatars/" + entityType + "/" + fileName;

        } catch (IOException e) {
            log.error("Could not store file for {} {}", prefix, entityId, e);
            throw new IOException("Could not store file: " + file.getOriginalFilename(), e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return ".jpg"; // расширение по умолчанию
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
                if (Files.exists(path)) {
                    return Files.readAllBytes(path);
                } else {
                    log.warn("File not found: {}", filePath);
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            log.error("Could not load file: {}", filePath, e);
            throw new IOException("Could not load file: " + filePath, e);
        }
    }

    // Универсальный метод для загрузки любого файла
    public String storeGenericFile(MultipartFile file, String subDirectory, String fileName) throws IOException {
        try {
            Path uploadPath = Paths.get(uploadDir, subDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return "/" + subDirectory + "/" + fileName;
        } catch (IOException e) {
            log.error("Could not store file in directory: {}", subDirectory, e);
            throw new IOException("Could not store file: " + file.getOriginalFilename(), e);
        }
    }
}