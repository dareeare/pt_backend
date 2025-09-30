package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.PatientMapper;
import medicalcenter.userservice.model.dto.PatientCreateEditDto;
import medicalcenter.userservice.model.dto.PatientReadDto;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class PatientService implements CrudService<PatientCreateEditDto, PatientReadDto> {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public List<PatientReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from PatientService");

        return patientMapper.toDto(patientRepository.findAll());
    }

    @Override
    public List<PatientReadDto> findAllByLastName(String lastName, Pageable pageable) {
        return patientMapper.toDto(patientRepository.findAllByLastName(lastName, pageable));
    }

    @Override
    public List<PatientReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        return patientMapper.toDto(patientRepository.findAllByLastFirstName(firstName, lastName, pageable));
    }

    @Override
    public PatientReadDto findOne(UUID id) {
        log.debug("finding patient with id: {}", id);

        Patient patient = patientRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return patientMapper.toDto(patient);
    }

    @Override
    public PatientReadDto findByFullName(String lastName, String firstName, String middleName) {
        Patient patient = patientRepository.findByFullName(lastName, firstName, middleName)
                .orElseThrow(NotFoundException::new);
        return patientMapper.toDto(patient);
    }

    @Override
    public PatientReadDto findByPhone(String phone) {
        Patient patient = patientRepository.findByPhone(phone).orElseThrow(NotFoundException::new);
        return patientMapper.toDto(patient);
    }

    @Override
    @Transactional
    public PatientReadDto save(PatientCreateEditDto patient) {
        log.debug("saving patient: {}", patient);

        Patient entity = patientMapper.toEntity(patient);
        return patientMapper.toDto(patientRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, PatientCreateEditDto updatedPatient) {
        log.debug("updating patient with id {}", id);

        Patient patient = patientRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        int updated = patientRepository.updateById(
                id,
                updatedPatient.lastName(),
                updatedPatient.firstName(),
                updatedPatient.middleName(),
                updatedPatient.phone(),
                updatedPatient.email(),
                updatedPatient.dateOfBirth(),
                updatedPatient.gender().charAt(0)
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting patient with id: {}", id);
        patientRepository.deleteById(id);
    }
}

