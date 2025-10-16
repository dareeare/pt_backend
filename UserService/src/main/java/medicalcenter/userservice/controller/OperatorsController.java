package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.operator.OperatorCreateEditDto;
import medicalcenter.userservice.model.dto.operator.OperatorReadDto;
import medicalcenter.userservice.service.impl.OperatorsService;
import medicalcenter.userservice.util.ControllerUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorsController {
    private final OperatorsService operatorsService;

    @GetMapping(params = {"!lastName", "!firstName", "!middleName", "!phone", "!email"})
    public ResponseEntity<List<OperatorReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(operatorsService.findAll(pageable));
    }

    @GetMapping(params = {"lastName", "!firstName", "!middleName", "!phone", "!email"})
    public ResponseEntity<List<OperatorReadDto>> getAllByLastName(@RequestParam String lastName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(operatorsService.findAllByLastName(lastName, pageable));
    }

    @GetMapping(params = {"lastName", "firstName", "!middleName", "!phone", "!email"})
    public ResponseEntity<List<OperatorReadDto>> getAllByLastFirstName(@RequestParam String lastName, @RequestParam String firstName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(operatorsService.findAllByLastFirstName(lastName, firstName, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperatorReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(operatorsService.findOne(id));
    }

    @GetMapping(params = {"lastName", "firstName", "middleName", "!phone", "!email"})
    public ResponseEntity<OperatorReadDto> getByFullName(
            @RequestParam String lastName,
            @RequestParam String firstName,
            @RequestParam String middleName) {
        return ResponseEntity.ok(operatorsService.findByFullName(lastName, firstName, middleName));
    }

    @GetMapping(params = {"!lastName", "!firstName", "!middleName", "phone", "!email"})
    public ResponseEntity<OperatorReadDto> getByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(operatorsService.findByPhone(phone));
    }

    @GetMapping(params = {"!lastName", "!firstName", "!middleName", "!phone", "email"})
    public ResponseEntity<OperatorReadDto> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(operatorsService.findByEmail(email));
    }

    @PostMapping
    public ResponseEntity<OperatorReadDto> createOperator(@RequestBody @Valid OperatorCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operatorsService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOperator(@PathVariable UUID id, @RequestBody @Valid OperatorCreateEditDto dto) {
        operatorsService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperator(@PathVariable UUID id) {
        operatorsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}