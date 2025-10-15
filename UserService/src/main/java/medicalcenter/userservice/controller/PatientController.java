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
import medicalcenter.userservice.model.dto.patient.*;
import medicalcenter.userservice.service.impl.PatientService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Log4j2
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
}