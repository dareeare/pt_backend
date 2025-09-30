package medicalcenter.userservice.service.impl;

import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Log4j2
public class DoctorService {
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> findAll() {
        log.debug("findAll() method is called from DoctorService");
        return doctorRepository.findAll();
    }

    public Doctor findOne(UUID id) {
        log.debug("finding doctor by id: {}", id);
        Optional<Doctor> foundPatient = doctorRepository.findById(id);
        return foundPatient.orElse(null);
    }

    @Transactional
    public void save(Doctor person) {
        log.debug("saving doctor: {}", person);
        doctorRepository.save(person);
    }

    @Transactional
    public void update(UUID id, Doctor updatedDoctor) {
        log.debug("updating doctor's id from {} to {}", id, updatedDoctor.getId());
        updatedDoctor.setId(id);
        doctorRepository.save(updatedDoctor);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("deleting doctor with id: {}", id);
        doctorRepository.deleteById(id);
    }
}