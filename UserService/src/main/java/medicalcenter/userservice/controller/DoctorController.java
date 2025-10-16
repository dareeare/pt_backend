package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medicalcenter.userservice.model.dto.doctor.DoctorCreateEditDto;
import medicalcenter.userservice.model.dto.doctor.DoctorReadDto;
import medicalcenter.userservice.service.impl.DoctorService;
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

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping(params = {"!lastName", "!lastName", "!middleName", "!phone"})
    public ResponseEntity<List<DoctorReadDto>> getAll(Pageable pageable) {
        return ControllerUtil.getListResponseEntity(doctorService.findAll(pageable));
    }

    @GetMapping(params = {"lastName", "!firstName", "!middleName", "!phone"})
    public ResponseEntity<List<DoctorReadDto>> getAllByLastName(@RequestParam String lastName, Pageable pageable) {
        return ControllerUtil.getListResponseEntity(doctorService.findAllByLastName(lastName, pageable));
    }

    @GetMapping(params = {"lastName", "firstName", "!middleName", "!phone"})
    public ResponseEntity<List<DoctorReadDto>> getAllByLastFirstName(
            @RequestParam String lastName,
            @RequestParam String firstName,
            Pageable pageable) {
        return ControllerUtil.getListResponseEntity(doctorService.findAllByLastFirstName(lastName, firstName, pageable));

    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorReadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.findOne(id));
    }

    @GetMapping(params = {"lastName", "firstName", "middleName", "!phone"})
    public ResponseEntity<DoctorReadDto> getByFullName(
            @RequestParam String lastName,
            @RequestParam String firstName,
            @RequestParam String middleName) {
        return ResponseEntity.ok(doctorService.findByFullName(lastName, firstName, middleName));
    }

    @GetMapping(params = {"!lastName", "!firstName", "!middleName", "phone"})
    public ResponseEntity<DoctorReadDto> getByPhone(@RequestParam String phone) {
        return ResponseEntity.ok(doctorService.findByPhone(phone));
    }

    @PostMapping
    public ResponseEntity<DoctorReadDto> createDoctor(@RequestBody @Valid DoctorCreateEditDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDoctor(@PathVariable UUID id, @RequestBody @Valid DoctorCreateEditDto dto) {
        doctorService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
