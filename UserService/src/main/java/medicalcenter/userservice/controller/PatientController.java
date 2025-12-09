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
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.model.dto.patient.*;
import medicalcenter.userservice.service.impl.PatientService;
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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patients Management", description = "API для управления пациентами медицинского центра")
public class PatientController {
    private final PatientService patientService;

    @Operation(
            summary = "Получить всех пациентов",
            description = "Возвращает список всех пациентов с поддержкой пагинации и сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка пациентов",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(params = {"!lastName", "!firstName", "!phone", "!middleName"})
    public ResponseEntity<List<PatientReadDto>> getPatients(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<PatientReadDto> all = patientService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск пациентов по фамилии",
            description = "Возвращает список пациентов с указанной фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по фамилии"),
            @ApiResponse(responseCode = "404", description = "Пациенты с указанной фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "!firstName", "!phone", "!middleName"})
    public ResponseEntity<List<PatientReadDto>> getAllByLastName(
            @Parameter(description = "Фамилия пациента", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<PatientReadDto> all = patientService.findAllByLastName(lastName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск пациентов по имени и фамилии",
            description = "Возвращает список пациентов с указанными именем и фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по имени и фамилии"),
            @ApiResponse(responseCode = "404", description = "Пациенты с указанными именем и фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "firstName", "!phone", "!middleName"})
    public ResponseEntity<List<PatientReadDto>> getAllByLastFirstName(
            @Parameter(description = "Фамилия пациента", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя пациента", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<PatientReadDto> all = patientService.findAllByLastFirstName(lastName, firstName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск пациента по телефону",
            description = "Возвращает пациента по номеру телефона"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент найден",
                    content = @Content(schema = @Schema(implementation = PatientReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Пациент с указанным телефоном не найден")
    })
    @GetMapping(params = {"phone", "!lastName", "!firstName", "!middleName"})
    public ResponseEntity<PatientReadDto> getPatientByPhone(
            @Parameter(
                    description = "Номер телефона пациента",
                    required = true,
                    example = "80291234567",
                    schema = @Schema(pattern = "^80(29|17|33|44|25)\\d{7}$")
            )
            @RequestParam String phone) {
        return ResponseEntity.ok(patientService.findByPhone(phone));
    }

    @Operation(
            summary = "Получить пациента по ID",
            description = "Возвращает пациента по его уникальному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент найден",
                    content = @Content(schema = @Schema(implementation = PatientReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Пациент с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientReadDto> getPatient(
            @Parameter(
                    description = "UUID пациента",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(patientService.findOne(id));
    }

    @Operation(
            summary = "Поиск пациента по полному ФИО",
            description = "Возвращает пациента по фамилии, имени и отчеству"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент найден",
                    content = @Content(schema = @Schema(implementation = PatientReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Пациент с указанным ФИО не найден")
    })
    @GetMapping(params = {"lastName", "firstName", "middleName", "!phone"})
    public ResponseEntity<PatientReadDto> getByFullName(
            @Parameter(description = "Фамилия пациента", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя пациента", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Отчество пациента", required = true, example = "Сергеевич")
            @RequestParam String middleName) {
        return ResponseEntity.ok(patientService.findByFullName(lastName, firstName, middleName));
    }

    @Operation(
            summary = "Создать нового пациента",
            description = "Создает нового пациента в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пациент успешно создан",
                    content = @Content(schema = @Schema(implementation = PatientReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные пациента")
    })
    @PostMapping
    public ResponseEntity<PatientReadDto> createPatient(
            @Parameter(description = "Данные для создания пациента", required = true)
            @RequestBody @Valid PatientCreateEditDto dto) {
        PatientReadDto patient = patientService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @Operation(
            summary = "Обновить данные пациента",
            description = "Обновляет информацию о пациенте по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пациента успешно обновлены"),
            @ApiResponse(responseCode = "404", description = "Пациент с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePatient(
            @Parameter(description = "UUID пациента", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные пациента", required = true)
            @RequestBody @Valid PatientCreateEditDto dto) {
        patientService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить пациента",
            description = "Удаляет пациента из системы по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пациент успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пациент с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "UUID пациента", required = true)
            @PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Загрузить аватарку пациента",
            description = "Загружает аватарку для указанного пациента"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка успешно загружена"),
            @ApiResponse(responseCode = "404", description = "Пациент с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сохранении файла")
    })
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @Parameter(description = "UUID пациента", required = true)
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

            String avatarPath = patientService.updateAvatar(id, avatarFile);
            return ResponseEntity.ok(Map.of("avatarPath", avatarPath));

        } catch (IOException e) {
            log.error("Error uploading avatar for patient {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not upload file"));
        }
    }

    @Operation(
            summary = "Получить аватарку пациента",
            description = "Возвращает аватарку пациента в виде массива байтов"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка найдена"),
            @ApiResponse(responseCode = "404", description = "Пациент или аватарка не найдены")
    })
    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(
            @Parameter(description = "UUID пациента", required = true)
            @PathVariable UUID id) {

        try {
            byte[] avatar = patientService.getAvatar(id);
            if (avatar == null) {
                return ResponseEntity.notFound().build();
            }

            // Определяем Content-Type на основе расширения файла
            String contentType = "image/jpeg"; // по умолчанию
            PatientReadDto patient = patientService.findOne(id);
            if (patient.avatarPath() != null) {
                String avatarPath = patient.avatarPath().toLowerCase();
                if (avatarPath.endsWith(".png")) {
                    contentType = "image/png";
                } else if (avatarPath.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (avatarPath.endsWith(".webp")) {
                    contentType = "image/webp";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(avatar);

        } catch (IOException e) {
            log.error("Error loading avatar for patient {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Удалить аватарку пациента",
            description = "Удаляет аватарку указанного пациента"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Аватарка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<Void> deleteAvatar(
            @Parameter(description = "UUID пациента", required = true)
            @PathVariable UUID id) {

        patientService.deleteAvatar(id);
        return ResponseEntity.noContent().build();
    }
}