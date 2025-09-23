package medicalcenter.userservice.service;

import medicalcenter.userservice.model.Patient;
import medicalcenter.userservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findOne(UUID id) {
        Optional<Patient> foundPatient = patientRepository.findById(id);
        return foundPatient.orElse(null);
    }

    @Transactional
    public void save(Patient person) {
        patientRepository.save(person);
    }

    @Transactional
    public void update(UUID id, Patient updatedPatient) {
        updatedPatient.setId(id);
        patientRepository.save(updatedPatient);
    }

    @Transactional
    public void delete(UUID id) {
        patientRepository.deleteById(id);
    }
}

