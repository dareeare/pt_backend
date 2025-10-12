package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.timeslot.TimeSlotCreateEditDto;
import medicalcenter.userservice.model.dto.timeslot.TimeSlotReadDto;
import medicalcenter.userservice.service.impl.TimeSlotsService;
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
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotsController {
    private final TimeSlotsService timeSlotsService;

    @GetMapping(params = {"!doctorId", "!slotDate", "!available"})
    public ResponseEntity<List<TimeSlotReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(timeSlotsService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(timeSlotsService.findOne(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TimeSlotReadDto>> getByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(timeSlotsService.findByDoctorId(doctorId, pageable));
    }

    @GetMapping("/date/{slotDate}")
    public ResponseEntity<List<TimeSlotReadDto>> getBySlotDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slotDate,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(timeSlotsService.findBySlotDate(slotDate, pageable));
    }

    @GetMapping("/doctor/{doctorId}/date/{slotDate}")
    public ResponseEntity<List<TimeSlotReadDto>> getByDoctorIdAndSlotDate(
            @PathVariable UUID doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slotDate,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(timeSlotsService.findByDoctorIdAndSlotDate(doctorId, slotDate, pageable));
    }

    @GetMapping(path = "/available", params = {"!doctorId", "!slotDate"})
    public ResponseEntity<List<TimeSlotReadDto>> getAvailableSlots(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(timeSlotsService.findAvailableSlots(pageable));
    }

    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<List<TimeSlotReadDto>> getAvailableSlotsByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(timeSlotsService.findAvailableSlotsByDoctorId(doctorId, pageable));
    }

    @GetMapping("/doctor/{doctorId}/date/{slotDate}/available")
    public ResponseEntity<List<TimeSlotReadDto>> getAvailableSlotsByDoctorIdAndDate(
            @PathVariable UUID doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate slotDate,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(timeSlotsService.findAvailableSlotsByDoctorIdAndDate(doctorId, slotDate, pageable));
    }

    @PostMapping
    public ResponseEntity<TimeSlotReadDto> createTimeSlot(@RequestBody @Valid TimeSlotCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timeSlotsService.save(dto));
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<TimeSlotReadDto> releaseSlot(@PathVariable UUID id) {
        return ResponseEntity.ok(timeSlotsService.releaseSlot(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTimeSlot(@PathVariable UUID id, @RequestBody @Valid TimeSlotCreateEditDto dto) {
        timeSlotsService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable UUID id) {
        timeSlotsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}