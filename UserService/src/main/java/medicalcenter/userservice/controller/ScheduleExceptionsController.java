package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.scheduleexception.ScheduleExceptionCreateEditDto;
import medicalcenter.userservice.model.dto.scheduleexception.ScheduleExceptionReadDto;
import medicalcenter.userservice.service.impl.ScheduleExceptionsService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/schedule-exceptions")
@RequiredArgsConstructor
public class ScheduleExceptionsController {
    private final ScheduleExceptionsService scheduleExceptionsService;

    @GetMapping
    public ResponseEntity<List<ScheduleExceptionReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleExceptionsService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleExceptionReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(scheduleExceptionsService.findOne(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleExceptionReadDto>> getByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleExceptionsService.findByDoctorId(doctorId, pageable));
    }

    @GetMapping("/date/{exceptionDate}")
    public ResponseEntity<List<ScheduleExceptionReadDto>> getByExceptionDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate exceptionDate,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleExceptionsService.findByExceptionDate(exceptionDate, pageable));
    }

    @GetMapping("/doctor/{doctorId}/date/{exceptionDate}")
    public ResponseEntity<ScheduleExceptionReadDto> getByDoctorIdAndExceptionDate(
            @PathVariable UUID doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate exceptionDate) {
        return ResponseEntity.ok(scheduleExceptionsService.findByDoctorIdAndExceptionDate(doctorId, exceptionDate));
    }

    @GetMapping(path = "/working-day")
    public ResponseEntity<List<ScheduleExceptionReadDto>> getByIsWorkingDay(
            @RequestParam Boolean isWorkingDay,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleExceptionsService.findByIsWorkingDay(isWorkingDay, pageable));
    }

    @PostMapping
    public ResponseEntity<ScheduleExceptionReadDto> createException(@RequestBody @Valid ScheduleExceptionCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleExceptionsService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateException(@PathVariable UUID id, @RequestBody @Valid ScheduleExceptionCreateEditDto dto) {
        scheduleExceptionsService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteException(@PathVariable UUID id) {
        scheduleExceptionsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}