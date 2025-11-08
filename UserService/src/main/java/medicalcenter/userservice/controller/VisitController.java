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
import medicalcenter.userservice.model.dto.visit.VisitCreateEditDto;
import medicalcenter.userservice.model.dto.visit.VisitReadDto;
import medicalcenter.userservice.service.impl.VisitService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Visits Management", description = "API для управления визитами пациентов")
public class VisitController {
    private final VisitService visitService;

    @Operation(
            summary = "Получить все визиты",
            description = "Возвращает список всех визитов с поддержкой пагинации и сортировки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка визитов",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(params = {"!lastName", "!firstName", "!doctorId", "!patientId", "!status"})
    public ResponseEntity<List<VisitReadDto>> getVisits(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<VisitReadDto> all = visitService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск визитов по фамилии пациента",
            description = "Возвращает список визитов пациентов с указанной фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по фамилии пациента"),
            @ApiResponse(responseCode = "404", description = "Визиты с указанной фамилией пациента не найдены")
    })
    @GetMapping(params = {"lastName", "!firstName", "!doctorId", "!patientId", "!status"})
    public ResponseEntity<List<VisitReadDto>> getAllByPatientLastName(
            @Parameter(description = "Фамилия пациента", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<VisitReadDto> all = visitService.findAllByLastName(lastName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Поиск визитов по имени и фамилии пациента",
            description = "Возвращает список визитов пациентов с указанными именем и фамилией"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный поиск по имени и фамилии пациента"),
            @ApiResponse(responseCode = "404", description = "Визиты с указанными именем и фамилией пациента не найдены")
    })
    @GetMapping(params = {"lastName", "firstName", "!doctorId", "!patientId", "!status"})
    public ResponseEntity<List<VisitReadDto>> getAllByPatientLastFirstName(
            @Parameter(description = "Фамилия пациента", required = true, example = "Иванов")
            @RequestParam String lastName,
            @Parameter(description = "Имя пациента", required = true, example = "Иван")
            @RequestParam String firstName,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<VisitReadDto> all = visitService.findAllByLastFirstName(lastName, firstName, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Получить визит по ID",
            description = "Возвращает визит по его уникальному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Визит найден",
                    content = @Content(schema = @Schema(implementation = VisitReadDto.class))),
            @ApiResponse(responseCode = "404", description = "Визит с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VisitReadDto> getVisit(
            @Parameter(
                    description = "UUID визита",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID id) {
        return ResponseEntity.ok(visitService.findOne(id));
    }

    @Operation(
            summary = "Получить визиты врача",
            description = "Возвращает список визитов указанного врача"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Визиты врача найдены"),
            @ApiResponse(responseCode = "404", description = "Визиты для указанного врача не найдены")
    })
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<VisitReadDto>> getByDoctorId(
            @Parameter(
                    description = "UUID врача",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID doctorId,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<VisitReadDto> all = visitService.findByDoctorId(doctorId, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Получить визиты пациента",
            description = "Возвращает список визитов указанного пациента"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Визиты пациента найдены"),
            @ApiResponse(responseCode = "404", description = "Визиты для указанного пациента не найдены")
    })
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VisitReadDto>> getByPatientId(
            @Parameter(
                    description = "UUID пациента",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID patientId,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<VisitReadDto> all = visitService.findByPatientId(patientId, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Получить визиты по статусу",
            description = "Возвращает список визитов с указанным статусом"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Визиты по статусу найдены"),
            @ApiResponse(responseCode = "400", description = "Неверный статус"),
            @ApiResponse(responseCode = "404", description = "Визиты с указанным статусом не найдены")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<VisitReadDto>> getByStatus(
            @Parameter(
                    description = "Статус визита",
                    required = true,
                    example = "scheduled",
                    schema = @Schema(allowableValues = {"scheduled", "completed", "cancelled"})
            )
            @PathVariable String status,
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        List<VisitReadDto> all = visitService.findByStatus(status, pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Создать новый визит",
            description = "Создает новую запись на визит в системе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Визит успешно создан",
                    content = @Content(schema = @Schema(implementation = VisitReadDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные визита"),
            @ApiResponse(responseCode = "404", description = "Врач или пациент с указанным ID не найден")
    })
    @PostMapping
    public ResponseEntity<VisitReadDto> createVisit(
            @Parameter(description = "Данные для создания визита", required = true)
            @RequestBody @Valid VisitCreateEditDto dto) {
        VisitReadDto visit = visitService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(visit);
    }

    @Operation(
            summary = "Обновить данные визита",
            description = "Обновляет информацию о визите по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные визита успешно обновлены"),
            @ApiResponse(responseCode = "404", description = "Визит с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVisit(
            @Parameter(description = "UUID визита", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные визита", required = true)
            @RequestBody @Valid VisitCreateEditDto dto) {
        visitService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Удалить визит",
            description = "Удаляет визит из системы по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Визит успешно удален"),
            @ApiResponse(responseCode = "404", description = "Визит с указанным ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(
            @Parameter(description = "UUID визита", required = true)
            @PathVariable UUID id) {
        visitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}