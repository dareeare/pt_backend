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
import medicalcenter.userservice.model.dto.manager.ManagerCreateEditDto;
import medicalcenter.userservice.model.dto.manager.ManagerReadDto;
import medicalcenter.userservice.service.impl.ManagerService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Managers Management", description = "API для управления менеджерами медицинского центра")
public class ManagerController {
    private final ManagerService managerService;

    @Operation(
            summary = "Получить всех менеджеров",
            description = "Возвращает список всех менеджеров с поддержкой пагинации и сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка менеджеров",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(params = {"!lastName", "!firstName", "!phone", "!middleName"})
    public ResponseEntity<List<ManagerReadDto>> getManagers(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<ManagerReadDto> all = managerService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск менеджеров по фамилии",
            description = "Возвращает список менеджеров с указанной фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по фамилии"),
            @ApiResponse(responseCode = "404", description = "Менеджеры с указанной фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "!firstName", "!phone", "!middleName"})
    public ResponseEntity<List<ManagerReadDto>> getAllByLastName(
            @Parameter(description = "Фамилия менеджера", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<ManagerReadDto> all = managerService.findAllByLastName(lastName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск менеджеров по имени и фамилии",
            description = "Возвращает список менеджеров с указанными именем и фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по имени и фамилии"),
            @ApiResponse(responseCode = "404", description = "Менеджеры с указанными именем и фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "firstName", "!phone", "!middleName"})
    public ResponseEntity<List<ManagerReadDto>> getAllByLastFirstName(
            @Parameter(description = "Фамилия менеджера", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя менеджера", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<ManagerReadDto> all = managerService.findAllByLastFirstName(lastName, firstName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск менеджера по телефону",
            description = "Возвращает менеджера по номеру телефона"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджер найден",
                    content = @Content(schema = @Schema(implementation = ManagerReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Менеджер с указанным телефоном не найден")
    })
    @GetMapping(params = {"phone", "!lastName", "!firstName", "!middleName"})
    public ResponseEntity<ManagerReadDto> getManagerByPhone(
            @Parameter(
                    description = "Номер телефона менеджера",
                    required = true,
                    example = "80291234567",
                    schema = @Schema(pattern = "^80(29|17|33|44|25)\\d{7}$")
            )
            @RequestParam String phone) {
        return ResponseEntity.ok(managerService.findByPhone(phone));
    }

    @Operation(
            summary = "Получить менеджера по ID",
            description = "Возвращает менеджера по его уникальному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджер найден",
                    content = @Content(schema = @Schema(implementation = ManagerReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Менеджер с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManagerReadDto> getManager(
            @Parameter(
                    description = "UUID менеджера",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(managerService.findOne(id));
    }

    @Operation(
            summary = "Поиск менеджера по полному ФИО",
            description = "Возвращает менеджера по фамилии, имени и отчеству"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджер найден",
                    content = @Content(schema = @Schema(implementation = ManagerReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Менеджер с указанным ФИО не найден")
    })
    @GetMapping(params = {"lastName", "firstName", "middleName", "!phone"})
    public ResponseEntity<ManagerReadDto> getByFullName(
            @Parameter(description = "Фамилия менеджера", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя менеджера", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Отчество менеджера", required = true, example = "Сергеевич")
            @RequestParam String middleName) {
        return ResponseEntity.ok(managerService.findByFullName(lastName, firstName, middleName));
    }

    @Operation(
            summary = "Создать нового менеджера",
            description = "Создает нового менеджера в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Менеджер успешно создан",
                    content = @Content(schema = @Schema(implementation = ManagerReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные менеджера")
    })
    @PostMapping
    public ResponseEntity<ManagerReadDto> createManager(
            @Parameter(description = "Данные для создания менеджера", required = true)
            @RequestBody @Valid ManagerCreateEditDto dto) {
        ManagerReadDto manager = managerService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(manager);
    }

    @Operation(
            summary = "Обновить данные менеджера",
            description = "Обновляет информацию о менеджере по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные менеджера успешно обновлены"),
            @ApiResponse(responseCode = "404", description = "Менеджер с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateManager(
            @Parameter(description = "UUID менеджера", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные менеджера", required = true)
            @RequestBody @Valid ManagerCreateEditDto dto) {
        managerService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить менеджера",
            description = "Удаляет менеджера из системы по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Менеджер успешно удален"),
            @ApiResponse(responseCode = "404", description = "Менеджер с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManager(
            @Parameter(description = "UUID менеджера", required = true)
            @PathVariable UUID id) {
        managerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Загрузить аватарку менеджера",
            description = "Загружает аватарку для указанного менеджера. Поддерживаемые форматы: JPEG, PNG, GIF, WEBP. Максимальный размер: 5MB."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка успешно загружена"),
            @ApiResponse(responseCode = "404", description = "Менеджер с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла или превышен размер"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сохранении файла")
    })
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @Parameter(description = "UUID менеджера", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Файл аватарки", required = true)
            @RequestParam("avatarFile") MultipartFile avatarFile) {

        try {
            if (avatarFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }

            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "File size must be less than 5MB"));
            }

            String avatarPath = managerService.updateAvatar(id, avatarFile);
            return ResponseEntity.ok(Map.of(
                    "avatarPath", avatarPath,
                    "message", "Avatar uploaded successfully"
            ));

        } catch (IOException e) {
            log.error("Error uploading avatar for manager {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not upload file: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Получить аватарку менеджера",
            description = "Возвращает аватарку менеджера в виде массива байтов"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка найдена"),
            @ApiResponse(responseCode = "404", description = "Менеджер или аватарка не найдены")
    })
    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(
            @Parameter(description = "UUID менеджера", required = true)
            @PathVariable UUID id) {

        try {
            byte[] avatar = managerService.getAvatar(id);
            if (avatar == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = "image/jpeg"; // по умолчанию
            ManagerReadDto manager = managerService.findOne(id);
            if (manager.avatarPath() != null) {
                String avatarPath = manager.avatarPath().toLowerCase();
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
                    .header("Cache-Control", "max-age=3600") // Кэшируем на 1 час
                    .body(avatar);

        } catch (IOException e) {
            log.error("Error loading avatar for manager {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Удалить аватарку менеджера",
            description = "Удаляет аватарку указанного менеджера"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Менеджер не найден")
    })
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<Map<String, String>> deleteAvatar(
            @Parameter(description = "UUID менеджера", required = true)
            @PathVariable UUID id) {

        try {
            managerService.deleteAvatar(id);
            return ResponseEntity.ok(Map.of("message", "Avatar deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting avatar for manager {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not delete avatar"));
        }
    }

    @Operation(
            summary = "Поиск менеджера по email",
            description = "Возвращает менеджера по email адресу"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Менеджер найден",
                    content = @Content(schema = @Schema(implementation = ManagerReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Менеджер с указанным email не найден")
    })
    @GetMapping(params = {"email", "!lastName", "!firstName", "!middleName", "!phone"})
    public ResponseEntity<ManagerReadDto> getManagerByEmail(
            @Parameter(
                    description = "Email адрес менеджера",
                    required = true,
                    example = "manager@example.com"
            )
            @RequestParam String email) {
        return ResponseEntity.ok(managerService.findByEmail(email));
    }
}