package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.manager.ManagerCreateEditDto;
import medicalcenter.userservice.model.dto.manager.ManagerReadDto;
import medicalcenter.userservice.service.impl.ManagerService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    @GetMapping(params = {"!lastName", "!firstName", "!middleName", "!phone"})
    public ResponseEntity<List<ManagerReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(managerService.findAll(pageable));
    }

    @GetMapping(params = {"lastName", "!firstName", "!middleName", "!phone"})
    public ResponseEntity<List<ManagerReadDto>> getAllByLastName(@RequestParam String lastName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(managerService.findAllByLastName(lastName, pageable));
    }

    @GetMapping(params = {"lastName", "firstName", "!middleName", "!phone"})
    public ResponseEntity<List<ManagerReadDto>> getAllByLastFirstName(@RequestParam String lastName, @RequestParam String firstName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(managerService.findAllByLastFirstName(lastName, firstName, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManagerReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(managerService.findOne(id));
    }

    @GetMapping(params = {"lastName", "firstName", "middleName", "!phone"})
    public ResponseEntity<ManagerReadDto> getByFullName(
            @RequestParam String lastName,
            @RequestParam String firstName,
            @RequestParam String middleName) {
        return ResponseEntity.ok(managerService.findByFullName(lastName, firstName, middleName));
    }

    @GetMapping(params = {"!lastName", "!firstName", "!middleName", "phone"})
    public ResponseEntity<ManagerReadDto> getByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(managerService.findByPhone(phone));
    }

    @PostMapping
    public ResponseEntity<ManagerReadDto> createManager(@RequestBody @Valid ManagerCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(managerService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateManager(@PathVariable UUID id, @RequestBody @Valid ManagerCreateEditDto dto) {
        managerService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable UUID id) {
        managerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}