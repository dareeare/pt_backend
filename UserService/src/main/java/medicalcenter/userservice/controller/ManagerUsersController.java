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
import medicalcenter.userservice.model.dto.doctor.DoctorCreateEditDto;
import medicalcenter.userservice.model.dto.doctor.DoctorReadDto;
import medicalcenter.userservice.model.dto.manager.DoctorUpdateDto;
import medicalcenter.userservice.model.dto.manager.OperatorUpdateDto;
import medicalcenter.userservice.model.dto.manager.PatientUpdateDto;
import medicalcenter.userservice.model.dto.operator.OperatorCreateEditDto;
import medicalcenter.userservice.model.dto.operator.OperatorReadDto;
import medicalcenter.userservice.model.dto.patient.PatientCreateEditDto;
import medicalcenter.userservice.model.dto.patient.PatientReadDto;
import medicalcenter.userservice.service.impl.DoctorService;
import medicalcenter.userservice.service.impl.OperatorsService;
import medicalcenter.userservice.service.impl.PatientService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Manager Users Management", description = "API для управления профилями пользователей, операторов и врачей менеджером")
public class ManagerUsersController {
    private final PatientService patientService;
    private final OperatorsService operatorsService;
    private final DoctorService doctorService;

    // ============ USERS (PATIENTS) ============

    @Operation(
            summary = "Получить всех пользователей (пациентов)",
            description = "Возвращает список всех пользователей с поддержкой пагинации и сортировки. Доступно только менеджерам."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/users")
    public ResponseEntity<List<PatientReadDto>> getUsers(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        log.debug("Manager: Getting all users");
        List<PatientReadDto> all = patientService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Обновить профиль пользователя (пациента)",
            description = "Обновляет информацию о пользователе по указанному ID. Доступно только менеджерам."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль пользователя успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @RequestBody @Valid PatientUpdateDto updateDto) {
        log.debug("Manager: Updating user with id {}", id);

        // Получаем существующие данные
        PatientReadDto existing = patientService.findOne(id);

        // Создаем полный DTO, объединяя существующие данные с обновлениями
        PatientCreateEditDto fullDto = new PatientCreateEditDto(
                updateDto.firstName() != null ? updateDto.firstName() : existing.firstName(),
                updateDto.lastName() != null ? updateDto.lastName() : existing.lastName(),
                existing.middleName() != null ? existing.middleName() : "", // Сохраняем отчество
                updateDto.phone() != null ? updateDto.phone() : existing.phone(),
                existing.email() != null ? existing.email() : "", // Email не редактируется менеджером
                updateDto.dateOfBirth() != null ? updateDto.dateOfBirth() : existing.dateOfBirth(),
                existing.gender() != null && !existing.gender().isEmpty() ? existing.gender() : "O", // Сохраняем пол, по умолчанию O
                existing.avatarPath() != null ? existing.avatarPath() : "" // Сохраняем путь к аватарке
        );

        patientService.update(id, fullDto);
        return ResponseEntity.ok().build();
    }

    // ============ OPERATORS ============

    @Operation(
            summary = "Получить всех операторов",
            description = "Возвращает список всех операторов с поддержкой пагинации и сортировки. Доступно только менеджерам."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка операторов",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/operators")
    public ResponseEntity<List<OperatorReadDto>> getOperators(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        log.debug("Manager: Getting all operators");
        List<OperatorReadDto> all = operatorsService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Обновить профиль оператора",
            description = "Обновляет информацию об операторе по указанному ID. Доступно только менеджерам."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль оператора успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Оператор с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PutMapping("/operators/{id}")
    public ResponseEntity<Void> updateOperator(
            @Parameter(description = "UUID оператора", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные оператора", required = true)
            @RequestBody @Valid OperatorUpdateDto updateDto) {
        log.debug("Manager: Updating operator with id {}", id);

        // Получаем существующие данные
        OperatorReadDto existing = operatorsService.findOne(id);

        // Создаем полный DTO, объединяя существующие данные с обновлениями
        OperatorCreateEditDto fullDto = new OperatorCreateEditDto(
                updateDto.firstName() != null ? updateDto.firstName() : existing.firstName(),
                updateDto.lastName() != null ? updateDto.lastName() : existing.lastName(),
                existing.middleName() != null ? existing.middleName() : "", // Сохраняем отчество
                updateDto.dateOfBirth() != null ? updateDto.dateOfBirth() : existing.dateOfBirth(),
                updateDto.phone() != null ? updateDto.phone() : existing.phone(),
                existing.email() != null ? existing.email() : "", // Email не редактируется менеджером
                existing.avatarPath() != null ? existing.avatarPath() : "" // Сохраняем путь к аватарке
        );

        operatorsService.update(id, fullDto);
        return ResponseEntity.ok().build();
    }

    // ============ DOCTORS ============

    @Operation(
            summary = "Получить всех врачей",
            description = "Возвращает список всех врачей с поддержкой пагинации и сортировки. Доступно только менеджерам."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка врачей",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorReadDto>> getDoctors(
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {
        log.debug("Manager: Getting all doctors");
        List<DoctorReadDto> all = doctorService.findAll(pageable);
        return ControllerUtil.getListResponseEntity(all);
    }

    @Operation(
            summary = "Обновить профиль врача",
            description = "Обновляет информацию о враче по указанному ID. Доступно только менеджерам."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль врача успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Врач с указанным ID не найден"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PutMapping("/doctors/{id}")
    public ResponseEntity<Void> updateDoctor(
            @Parameter(description = "UUID врача", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Обновленные данные врача", required = true)
            @RequestBody @Valid DoctorUpdateDto updateDto) {
        log.debug("Manager: Updating doctor with id {}", id);

        // Получаем существующие данные
        DoctorReadDto existing = doctorService.findOne(id);

        // Создаем полный DTO, объединяя существующие данные с обновлениями
        DoctorCreateEditDto fullDto = new DoctorCreateEditDto(
                updateDto.firstName() != null ? updateDto.firstName() : existing.firstName(),
                updateDto.lastName() != null ? updateDto.lastName() : existing.lastName(),
                existing.middleName() != null ? existing.middleName() : "", // Сохраняем отчество
                updateDto.specialty() != null ? updateDto.specialty() : existing.specialty(),
                updateDto.phone() != null ? updateDto.phone() : existing.phone(),
                existing.email() != null ? existing.email() : "", // Email не редактируется менеджером
                existing.information() != null ? existing.information() : "", // Сохраняем дополнительную информацию
                existing.rating(), // Сохраняем рейтинг
                existing.avatarPath() != null ? existing.avatarPath() : "" // Сохраняем путь к аватарке
        );

        doctorService.update(id, fullDto);
        return ResponseEntity.ok().build();
    }
}
