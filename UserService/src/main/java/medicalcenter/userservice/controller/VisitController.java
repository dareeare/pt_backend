package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.visit.RescheduleVisitDto;
import medicalcenter.userservice.model.dto.visit.VisitCreateEditDto;
import medicalcenter.userservice.model.dto.visit.VisitReadDto;
import medicalcenter.userservice.service.impl.VisitService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;

    @GetMapping(params = {"!lastName", "!firstName", "!doctorId", "!patientId", "!status"})
    public ResponseEntity<List<VisitReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findAll(pageable));
    }

    @GetMapping(params = {"lastName", "!firstName", "!doctorId", "!patientId", "!status"})
    public ResponseEntity<List<VisitReadDto>> getAllByPatientLastName(@RequestParam String lastName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findAllByLastName(lastName, pageable));
    }

    @GetMapping(params = {"lastName", "firstName", "!doctorId", "!patientId", "!status"})
    public ResponseEntity<List<VisitReadDto>> getAllByPatientLastFirstName(@RequestParam String lastName, @RequestParam String firstName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findAllByLastFirstName(lastName, firstName, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(visitService.findOne(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<VisitReadDto>> getByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findByDoctorId(doctorId, pageable));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VisitReadDto>> getByPatientId(@PathVariable UUID patientId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findByPatientId(patientId, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VisitReadDto>> getByStatus(@PathVariable String status, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findByStatus(status, pageable));
    }

    @PostMapping
    public ResponseEntity<VisitReadDto> createVisit(@RequestBody @Valid VisitCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVisit(@PathVariable UUID id, @RequestBody @Valid VisitCreateEditDto dto) {
        visitService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable UUID id) {
        visitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}/past")
    public ResponseEntity<List<VisitReadDto>> getPastVisitsByPatientId(@PathVariable UUID patientId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findPastVisitsByPatientId(patientId, pageable));
    }

    @GetMapping("/patient/{patientId}/future")
    public ResponseEntity<List<VisitReadDto>> getFutureVisitsByPatientId(@PathVariable UUID patientId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findFutureVisitsByPatientId(patientId, pageable));
    }

    @GetMapping("/doctor/{doctorId}/past")
    public ResponseEntity<List<VisitReadDto>> getPastVisitsByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findPastVisitsByDoctorId(doctorId, pageable));
    }

    @GetMapping("/doctor/{doctorId}/future")
    public ResponseEntity<List<VisitReadDto>> getFutureVisitsByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findFutureVisitsByDoctorId(doctorId, pageable));
    }

    @GetMapping("/doctor/{doctorId}/patient/{patientId}/past")
    public ResponseEntity<List<VisitReadDto>> getPastVisitsByDoctorAndPatient(
            @PathVariable UUID doctorId, 
            @PathVariable UUID patientId, 
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(visitService.findPastVisitsByDoctorAndPatient(doctorId, patientId, pageable));
    }

    /**
     * Отменяет запись
     * Устанавливает статус "cancelled" и освобождает связанный слот времени
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<VisitReadDto> cancelVisit(@PathVariable UUID id) {
        return ResponseEntity.ok(visitService.cancel(id));
    }

    /**
     * Переносит запись на другое время/дату
     * Обновляет дату визита, освобождает старый слот и резервирует новый
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<VisitReadDto> rescheduleVisit(
            @PathVariable UUID id,
            @RequestBody @Valid RescheduleVisitDto dto) {
        return ResponseEntity.ok(visitService.reschedule(id, dto));
    }
}