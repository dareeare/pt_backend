package medicalcenter.userservice.service;

import medicalcenter.userservice.model.Doctor;
import medicalcenter.userservice.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DoctorService {
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findOne(UUID id) {
        Optional<Doctor> foundPatient = doctorRepository.findById(id);
        return foundPatient.orElse(null);
    }

    @Transactional
    public void save(Doctor person) {
        doctorRepository.save(person);
    }

    @Transactional
    public void update(UUID id, Doctor updatedDoctor) {
        updatedDoctor.setId(id);
        doctorRepository.save(updatedDoctor);
    }

    @Transactional
    public void delete(UUID id) {
        doctorRepository.deleteById(id);
    }
}