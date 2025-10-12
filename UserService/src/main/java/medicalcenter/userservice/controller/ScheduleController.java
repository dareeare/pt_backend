package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.schedule.ScheduleCreateEditDto;
import medicalcenter.userservice.model.dto.schedule.ScheduleReadDto;
import medicalcenter.userservice.service.impl.ScheduleService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping(params = {"!doctorLastName", "!doctorFirstName", "!doctorId", "!workDay"})
    public ResponseEntity<List<ScheduleReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleService.findAll(pageable));
    }

    @GetMapping(params = {"doctorLastName", "!doctorFirstName", "!doctorId", "!workDay"})
    public ResponseEntity<List<ScheduleReadDto>> getAllByDoctorLastName(@RequestParam String doctorLastName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleService.findAllByLastName(doctorLastName, pageable));
    }

    @GetMapping(params = {"doctorLastName", "doctorFirstName", "!doctorId", "!workDay"})
    public ResponseEntity<List<ScheduleReadDto>> getAllByDoctorLastFirstName(
            @RequestParam String doctorLastName,
            @RequestParam String doctorFirstName,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleService.findAllByLastFirstName(doctorLastName, doctorFirstName, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(scheduleService.findOne(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleReadDto>> getByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleService.findByDoctorId(doctorId, pageable));
    }

    @GetMapping("/day/{workDay}")
    public ResponseEntity<List<ScheduleReadDto>> getByWorkDay(@PathVariable LocalDate workDay, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleService.findByWorkDay(workDay, pageable));
    }

    @GetMapping("/doctor/{doctorId}/day/{workDay}")
    public ResponseEntity<List<ScheduleReadDto>> getByDoctorIdAndWorkDay(
            @PathVariable UUID doctorId,
            @PathVariable LocalDate workDay,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(scheduleService.findByDoctorIdAndWorkDay(doctorId, workDay, pageable));
    }

    @PostMapping
    public ResponseEntity<ScheduleReadDto> createSchedule(@RequestBody @Valid ScheduleCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSchedule(@PathVariable UUID id, @RequestBody @Valid ScheduleCreateEditDto dto) {
        scheduleService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable UUID id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}