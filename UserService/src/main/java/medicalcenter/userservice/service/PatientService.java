package medicalcenter.userservice.service;

import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class PatientService {
    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        log.debug("findAll() method is called from PatientService");
        return patientRepository.findAll();
    }

    public Patient findOne(UUID id) {
        log.debug("finding patient with id: {}", id);
        Optional<Patient> foundPatient = patientRepository.findById(id);
        return foundPatient.orElse(null);
    }

    @Transactional
    public void save(Patient patient) {
        log.debug("saving patient: {}", patient);
        patientRepository.save(patient);
    }

    @Transactional
    public void update(UUID id, Patient updatedPatient) {
        log.debug("updating patient's id from {} to {}", id, updatedPatient.getId());
        updatedPatient.setId(id);
        patientRepository.save(updatedPatient);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("deleting patient with id: {}", id);
        patientRepository.deleteById(id);
    }
}

