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
import medicalcenter.userservice.model.dto.operator.OperatorCreateEditDto;
import medicalcenter.userservice.model.dto.operator.OperatorReadDto;
import medicalcenter.userservice.service.impl.OperatorsService;
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
@RequestMapping("/api/operators")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Operators Management", description = "API для управления операторами call-центра медицинского центра")
public class OperatorsController {
    private final OperatorsService operatorsService;

    @Operation(
            summary = "Получить всех операторов",
            description = "Возвращает список всех операторов с поддержкой пагинации и сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка операторов",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(params = {"!lastName", "!firstName", "!phone", "!middleName", "!email"})
    public ResponseEntity<List<OperatorReadDto>> getOperators(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<OperatorReadDto> all = operatorsService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск операторов по фамилии",
            description = "Возвращает список операторов с указанной фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по фамилии"),
            @ApiResponse(responseCode = "404", description = "Операторы с указанной фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "!firstName", "!phone", "!middleName", "!email"})
    public ResponseEntity<List<OperatorReadDto>> getAllByLastName(
            @Parameter(description = "Фамилия оператора", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<OperatorReadDto> all = operatorsService.findAllByLastName(lastName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск операторов по имени и фамилии",
            description = "Возвращает список операторов с указанными именем и фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по имени и фамилии"),
            @ApiResponse(responseCode = "404", description = "Операторы с указанными именем и фамилией не найдены")
    })
    @GetMapping(params = {"lastName", "firstName", "!phone", "!middleName", "!email"})
    public ResponseEntity<List<OperatorReadDto>> getAllByLastFirstName(
            @Parameter(description = "Фамилия оператора", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя оператора", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<OperatorReadDto> all = operatorsService.findAllByLastFirstName(lastName, firstName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск оператора по телефону",
            description = "Возвращает оператора по номеру телефона"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оператор найден",
                    content = @Content(schema = @Schema(implementation = OperatorReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным телефоном не найден")
    })
    @GetMapping(params = {"phone", "!lastName", "!firstName", "!middleName", "!email"})
    public ResponseEntity<OperatorReadDto> getOperatorByPhone(
            @Parameter(
                    description = "Номер телефона оператора",
                    required = true,
                    example = "80291234567",
                    schema = @Schema(pattern = "^80(29|17|33|44|25)\\d{7}$")
            )
            @RequestParam String phone) {
        return ResponseEntity.ok(operatorsService.findByPhone(phone));
    }

    @Operation(
            summary = "Поиск оператора по email",
            description = "Возвращает оператора по email адресу"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оператор найден",
                    content = @Content(schema = @Schema(implementation = OperatorReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным email не найден")
    })
    @GetMapping(params = {"email", "!lastName", "!firstName", "!middleName", "!phone"})
    public ResponseEntity<OperatorReadDto> getOperatorByEmail(
            @Parameter(
                    description = "Email адрес оператора",
                    required = true,
                    example = "operator@example.com"
            )
            @RequestParam String email) {
        return ResponseEntity.ok(operatorsService.findByEmail(email));
    }

    @Operation(
            summary = "Получить оператора по ID",
            description = "Возвращает оператора по его уникальному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оператор найден",
                    content = @Content(schema = @Schema(implementation = OperatorReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OperatorReadDto> getOperator(
            @Parameter(
                    description = "UUID оператора",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(operatorsService.findOne(id));
    }

    @Operation(
            summary = "Поиск оператора по полному ФИО",
            description = "Возвращает оператора по фамилии, имени и отчеству"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Оператор найден",
                    content = @Content(schema = @Schema(implementation = OperatorReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным ФИО не найден")
    })
    @GetMapping(params = {"lastName", "firstName", "middleName", "!phone", "!email"})
    public ResponseEntity<OperatorReadDto> getByFullName(
            @Parameter(description = "Фамилия оператора", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя оператора", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Отчество оператора", required = true, example = "Сергеевич")
            @RequestParam String middleName) {
        return ResponseEntity.ok(operatorsService.findByFullName(lastName, firstName, middleName));
    }

    @Operation(
            summary = "Создать нового оператора",
            description = "Создает нового оператора в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Оператор успешно создан",
                    content = @Content(schema = @Schema(implementation = OperatorReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные оператора")
    })
    @PostMapping
    public ResponseEntity<OperatorReadDto> createOperator(
            @Parameter(description = "Данные для создания оператора", required = true)
            @RequestBody @Valid OperatorCreateEditDto dto) {
        OperatorReadDto operator = operatorsService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(operator);
    }

    @Operation(
            summary = "Обновить данные оператора",
            description = "Обновляет информацию об операторе по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные оператора успешно обновлены"),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOperator(
            @Parameter(description = "UUID оператора", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные оператора", required = true)
            @RequestBody @Valid OperatorCreateEditDto dto) {
        operatorsService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить оператора",
            description = "Удаляет оператора из системы по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Оператор успешно удален"),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperator(
            @Parameter(description = "UUID оператора", required = true)
            @PathVariable UUID id) {
        operatorsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Загрузить аватарку оператора",
            description = "Загружает аватарку для указанного оператора"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка успешно загружена"),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла"),
            @ApiResponse(responseCode = "500", description = "Ошибка при сохранении файла")
    })
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @Parameter(description = "UUID оператора", required = true)
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

            String avatarPath = operatorsService.updateAvatar(id, avatarFile);
            return ResponseEntity.ok(Map.of(
                    "avatarPath", avatarPath,
                    "message", "Avatar uploaded successfully"
            ));

        } catch (IOException e) {
            log.error("Error uploading avatar for operator {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not upload file: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Получить аватарку оператора",
            description = "Возвращает аватарку оператора в виде массива байтов"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватарка найдена"),
            @ApiResponse(responseCode = "404", description = "Оператор или аватарка не найдены")
    })
    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(
            @Parameter(description = "UUID оператора", required = true)
            @PathVariable UUID id) {

        try {
            byte[] avatar = operatorsService.getAvatar(id);
            if (avatar == null) {
                return ResponseEntity.notFound().build();
            }

            String contentType = "image/jpeg"; // по умолчанию
            OperatorReadDto operator = operatorsService.findOne(id);
            if (operator.avatarPath() != null) {
                String avatarPath = operator.avatarPath().toLowerCase();
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
            log.error("Error loading avatar for operator {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Удалить аватарку оператора",
            description = "Удаляет аватарку указанного оператора"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Аватарка успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Оператор не найден")
    })
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<Map<String, String>> deleteAvatar(
            @Parameter(description = "UUID оператора", required = true)
            @PathVariable UUID id) {

        try {
            operatorsService.deleteAvatar(id);
            return ResponseEntity.ok(Map.of("message", "Avatar deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting avatar for operator {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not delete avatar"));
        }
    }
}