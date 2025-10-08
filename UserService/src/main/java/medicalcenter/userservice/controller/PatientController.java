package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.model.dto.PatientCreateEditDto;
import medicalcenter.userservice.model.dto.patient.*;
import medicalcenter.userservice.service.impl.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Log4j2
public class PatientController {
    private final PatientService patientService;

    @GetMapping(params = {"!lastName", "!firstName", "!phone"})
    public ResponseEntity<List<PatientReadDto>> getPatients(Pageable pageable) {
        List<PatientReadDto> all = patientService.findAll(pageable);
        return getListResponseEntity(all);
    }

    @GetMapping(params = {"lastName", "!firstName", "!phone"})
    public ResponseEntity<List<PatientReadDto>> getAllByLastName(@RequestParam String lastName, Pageable pageable) {
        List<PatientReadDto> all = patientService.findAllByLastName(lastName, pageable);
        return getListResponseEntity(all);
    }

    @GetMapping(params = {"lastName", "firstName", "!phone"})
    public ResponseEntity<List<PatientReadDto>> getAllByLastFirstName(
            @RequestParam String lastName,
            @RequestParam String firstName,
            Pageable pageable) {
        List<PatientReadDto> all = patientService.findAllByLastFirstName(lastName, firstName, pageable);
        return getListResponseEntity(all);
    }

    @GetMapping(params = {"phone", "!lastName", "!firstName"})
    public ResponseEntity<PatientReadDto> getPatientByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(patientService.findByPhone(phone));
    }

    private ResponseEntity<List<PatientReadDto>> getListResponseEntity(List<PatientReadDto> all) {
        if (all.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientReadDto> getPatient(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<PatientReadDto> createPatient(@RequestBody @Valid PatientCreateEditDto dto) {
        PatientReadDto patient = patientService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePatient(@PathVariable UUID id, @RequestBody @Valid PatientCreateEditDto dto) {
        patientService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
