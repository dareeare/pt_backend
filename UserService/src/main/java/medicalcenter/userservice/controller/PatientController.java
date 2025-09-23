package medicalcenter.userservice.controller;

import medicalcenter.userservice.model.Patient;
import medicalcenter.userservice.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Patient> getPeople() {
        return patientService.findAll(); // Jackson конвертирует эти объекты в JSON
    }

    @GetMapping("/{id}")
    public Patient getPerson(@PathVariable("id") UUID id) {
        return patientService.findOne(id); // Jackson конвертирует в JSON
    }
}
