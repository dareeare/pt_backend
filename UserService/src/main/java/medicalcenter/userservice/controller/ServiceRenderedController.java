package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.servicerendered.ServiceRenderedCreateEditDto;
import medicalcenter.userservice.model.dto.servicerendered.ServiceRenderedReadDto;
import medicalcenter.userservice.service.impl.ServiceRenderedService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/services-rendered")
@RequiredArgsConstructor
public class ServiceRenderedController {
    private final ServiceRenderedService serviceRenderedService;

    @GetMapping
    public ResponseEntity<List<ServiceRenderedReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(serviceRenderedService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRenderedReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(serviceRenderedService.findOne(id));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<ServiceRenderedReadDto>> getByVisitId(@PathVariable UUID visitId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(serviceRenderedService.findByVisitId(visitId, pageable));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ServiceRenderedReadDto>> getByServiceId(@PathVariable UUID serviceId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(serviceRenderedService.findByServiceId(serviceId, pageable));
    }

    @GetMapping("/visit/{visitId}/total-cost")
    public ResponseEntity<Double> calculateTotalCostByVisitId(@PathVariable UUID visitId) {
        return ResponseEntity.ok(serviceRenderedService.calculateTotalCostByVisitId(visitId));
    }

    @PostMapping
    public ResponseEntity<ServiceRenderedReadDto> createServiceRendered(@RequestBody @Valid ServiceRenderedCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceRenderedService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateServiceRendered(@PathVariable UUID id, @RequestBody @Valid ServiceRenderedCreateEditDto dto) {
        serviceRenderedService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceRendered(@PathVariable UUID id) {
        serviceRenderedService.delete(id);
        return ResponseEntity.noContent().build();
    }
}