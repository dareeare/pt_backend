package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.PatientMapper;
import medicalcenter.userservice.model.dto.patient.*;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.service.CrudService;
import medicalcenter.userservice.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PatientService implements CrudService<PatientCreateEditDto, PatientReadDto> {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final FileStorageService fileStorageService;

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
        return patientMapper.toDto(patientRepository.findAllByLastFirstName(lastName, firstName, pageable));
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
                updatedPatient.gender(),
                updatedPatient.avatarPath()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Transactional
    public String updateAvatar(UUID id, MultipartFile avatarFile) throws IOException {
        log.debug("updating avatar for patient with id {}", id);

        // Проверяем, что пациент существует
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        // Удаляем старую аватарку, если она существует
        if (patient.getAvatarPath() != null) {
            fileStorageService.deleteFile(patient.getAvatarPath());
        }

        // Сохраняем новую аватарку
        String avatarPath = fileStorageService.storePatientFile(avatarFile, id);

        // Обновляем путь в базе данных
        int updated = patientRepository.updateAvatarPath(id, avatarPath);
        if (updated == 0) {
            throw new UpdateException(id);
        }

        return avatarPath;
    }

    @Transactional
    public void deleteAvatar(UUID id) {
        log.debug("deleting avatar for patient with id {}", id);

        Patient patient = patientRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (patient.getAvatarPath() != null) {
            // Удаляем файл
            fileStorageService.deleteFile(patient.getAvatarPath());

            // Обновляем базу данных
            patientRepository.updateAvatarPath(id, null);
        }
    }

    public byte[] getAvatar(UUID id) throws IOException {
        log.debug("getting avatar for patient with id {}", id);

        Patient patient = patientRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (patient.getAvatarPath() == null) {
            return null;
        }

        return fileStorageService.loadFile(patient.getAvatarPath());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting patient with id: {}", id);
        patientRepository.deleteById(id);
    }
}

