package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.service.ServiceCreateEditDto;
import medicalcenter.userservice.model.dto.service.ServiceReadDto;
import medicalcenter.userservice.service.impl.ServiceService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;

    @GetMapping(params = {"!name", "!minCost", "!maxCost", "!doctorId"})
    public ResponseEntity<List<ServiceReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(serviceService.findAll(pageable));
    }

    @GetMapping(params = {"name", "!minCost", "!maxCost", "!doctorId"})
    public ResponseEntity<List<ServiceReadDto>> getAllByName(@RequestParam String name, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(serviceService.findAllByLastName(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(serviceService.findOne(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ServiceReadDto>> getByDoctorId(@PathVariable UUID doctorId, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(serviceService.findByDoctorId(doctorId, pageable));
    }

    @GetMapping(path = "/cost", params = {"minCost", "maxCost"})
    public ResponseEntity<List<ServiceReadDto>> getByCostBetween(
            @RequestParam Double minCost,
            @RequestParam Double maxCost,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(serviceService.findByCostBetween(minCost, maxCost, pageable));
    }

    @PostMapping
    public ResponseEntity<ServiceReadDto> createService(@RequestBody @Valid ServiceCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateService(@PathVariable UUID id, @RequestBody @Valid ServiceCreateEditDto dto) {
        serviceService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}