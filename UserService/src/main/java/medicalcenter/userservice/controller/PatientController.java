package medicalcenter.userservice.controller;

import jakarta.validation.Valid;
import medicalcenter.userservice.model.Patient;
import medicalcenter.userservice.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping()
    public List<Patient> getPatients() {
        return patientService.findAll(); // Jackson конвертирует эти объекты в JSON
    }

    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable("id") UUID id) {
        return patientService.findOne(id); // Jackson конвертирует в JSON
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createPatient(@RequestBody @Valid Patient patient,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            List<FieldError> errorList = bindingResult.getFieldErrors();
            for (FieldError errorMsg : errorList) {
                errors.append(errorMsg.getField())
                        .append(": ")
                        .append(errorMsg.getDefaultMessage())
                        .append(";\n");
            }
        }
        patientService.save(patient);
        //sends HTTP with status 200 and empty body
        return ResponseEntity.ok(HttpStatus.OK);
        //возможно придется заменить ResponseEntity<HttpStatus> на Patient
    }

}
