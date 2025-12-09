package medicalcenter.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.model.dto.doctor.DoctorCreateEditDto;
import medicalcenter.userservice.model.dto.doctor.DoctorReadDto;
import medicalcenter.userservice.service.impl.DoctorService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import medicalcenter.userservice.model.dto.AvatarUploadDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Doctors Management", description = "API для управления врачами медицинского центра")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(
            summary = "Получить всех врачей",
            description = "Возвращает список всех врачей с поддержкой пагинации и сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка врачей",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(params = {"!lastName", "!firstName", "!phone", "!middleName"})
    public ResponseEntity<List<DoctorReadDto>> getDoctors(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<DoctorReadDto> all = doctorService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск врачей по фамилии",
            description = "Возвращает список врачей с указанной фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по фамилии"),
            @ApiResponse(responseCode = "404", description = "Врачи с указанной фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "!firstName", "!phone", "!middleName"})
    public ResponseEntity<List<DoctorReadDto>> getAllByLastName(
            @Parameter(description = "Фамилия врача", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<DoctorReadDto> all = doctorService.findAllByLastName(lastName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск врачей по имени и фамилии",
            description = "Возвращает список врачей с указанными именем и фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по имени и фамилии"),
            @ApiResponse(responseCode = "404", description = "Врачи с указанными именем и фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "firstName", "!phone", "!middleName"})
    public ResponseEntity<List<DoctorReadDto>> getAllByLastFirstName(
            @Parameter(description = "Фамилия врача", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя врача", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<DoctorReadDto> all = doctorService.findAllByLastFirstName(lastName, firstName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск врача по телефону",
            description = "Возвращает врача по номеру телефона"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Врач найден",
                    content = @Content(schema = @Schema(implementation = DoctorReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Врач с указанным телефоном не найден")
    })
    @GetMapping(params = {"phone", "!lastName", "!firstName", "!middleName"})
    public ResponseEntity<DoctorReadDto> getDoctorByPhone(
            @Parameter(
                    description = "Номер телефона врача",
                    required = true,
                    example = "80291234567",
                    schema = @Schema(pattern = "^80(29|17|33|44|25)\\d{7}$")
            )
            @RequestParam String phone) {
        return ResponseEntity.ok(doctorService.findByPhone(phone));
    }

    @Operation(
            summary = "Получить врача по ID",
            description = "Возвращает врача по его уникальному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Врач найден",
                    content = @Content(schema = @Schema(implementation = DoctorReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Врач с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorReadDto> getDoctor(
            @Parameter(
                    description = "UUID врача",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.findOne(id));
    }

    @Operation(
            summary = "Поиск врача по полному ФИО",
            description = "Возвращает врача по фамилии, имени и отчеству"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Врач найден",
                    content = @Content(schema = @Schema(implementation = DoctorReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Врач с указанным ФИО не найден")
    })
    @GetMapping(params = {"lastName", "firstName", "middleName", "!phone"})
    public ResponseEntity<DoctorReadDto> getByFullName(
            @Parameter(description = "Фамилия врача", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя врача", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Отчество врача", required = true, example = "Сергеевич")
            @RequestParam String middleName) {
        return ResponseEntity.ok(doctorService.findByFullName(lastName, firstName, middleName));
    }

    @Operation(
            summary = "Создать нового врача",
            description = "Создает нового врача в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Врач успешно создан",
                    content = @Content(schema = @Schema(implementation = DoctorReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные врача")
    })
    @PostMapping
    // Разрешаем создание врачей, так как этот метод вызывается из AuthService при синхронизации
    // В продакшене здесь должна быть проверка на специальную роль или scope
    public ResponseEntity<DoctorReadDto> createDoctor(
            @Parameter(description = "Данные для создания врача", required = true)
            @RequestBody @Valid DoctorCreateEditDto dto) {
        DoctorReadDto doctor = doctorService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    @Operation(
            summary = "Обновить данные врача",
            description = "Обновляет информацию о враче по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные врача успешно обновлены"),
            @ApiResponse(responseCode = "404", description = "Врач с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDoctor(
            @Parameter(description = "UUID врача", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные врача", required = true)
            @RequestBody @Valid DoctorCreateEditDto dto) {
        doctorService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить врача",
            description = "Удаляет врача из системы по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Врач успешно удален"),
            @ApiResponse(responseCode = "404", description = "Врач с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @Parameter(description = "UUID врача", required = true)
            @PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Загрузить аватарку врача",
            description = "Загружает аватарку для указанного врача"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка успешно загружена"),
            @ApiResponse(responseCode = "404", description = "Врач с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сохранении файла")
    })
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @Parameter(description = "UUID врача", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Файл аватарки", required = true)
            @RequestParam("avatarFile") MultipartFile avatarFile) {

        try {
            // Проверяем тип файла
            if (avatarFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }

            String avatarPath = doctorService.updateAvatar(id, avatarFile);
            return ResponseEntity.ok(Map.of("avatarPath", avatarPath));

        } catch (IOException e) {
            log.error("Error uploading avatar for doctor {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not upload file"));
        }
    }

    @Operation(
            summary = "Получить аватарку врача",
            description = "Возвращает аватарку врача в виде массива байтов"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка найдена"),
            @ApiResponse(responseCode = "404", description = "Врач или аватарка не найдены")
    })
    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(
            @Parameter(description = "UUID врача", required = true)
            @PathVariable UUID id) {

        try {
            byte[] avatar = doctorService.getAvatar(id);
            if (avatar == null) {
                return ResponseEntity.notFound().build();
            }

            // Определяем Content-Type на основе расширения файла
            String contentType = "image/jpeg"; // по умолчанию
            DoctorReadDto doctor = doctorService.findOne(id);
            if (doctor.avatarPath() != null) {
                String avatarPath = doctor.avatarPath().toLowerCase();
                if (avatarPath.endsWith(".png")) {
                    contentType = "image/png";
                } else if (avatarPath.endsWith(".gif")) {
                    contentType = "image/gif";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(avatar);

        } catch (IOException e) {
            log.error("Error loading avatar for doctor {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Удалить аватарку врача",
            description = "Удаляет аватарку указанного врача"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Аватарка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Врач не найден")
    })
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<Void> deleteAvatar(
            @Parameter(description = "UUID врача", required = true)
            @PathVariable UUID id) {

        doctorService.deleteAvatar(id);
        return ResponseEntity.noContent().build();
    }
}