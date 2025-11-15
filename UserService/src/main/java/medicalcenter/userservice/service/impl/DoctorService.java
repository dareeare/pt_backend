package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.DoctorMapper;
import medicalcenter.userservice.model.dto.doctor.DoctorCreateEditDto;
import medicalcenter.userservice.model.dto.doctor.DoctorReadDto;
import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import medicalcenter.userservice.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.UUID;
import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class DoctorService implements CrudService<DoctorCreateEditDto, DoctorReadDto> {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<DoctorReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from DoctorService");
        return doctorMapper.toDto(doctorRepository.findAll(pageable).getContent());
    }

    @Override
    public List<DoctorReadDto> findAllByLastName(String lastName, Pageable pageable) {
        return doctorMapper.toDto(doctorRepository.findAllByLastName(lastName, pageable));
    }

    @Override
    public List<DoctorReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        return doctorMapper.toDto(doctorRepository.findAllByLastFirstName(firstName, lastName, pageable));
    }

    @Override
    public DoctorReadDto findOne(UUID id) {
        log.debug("finding doctor with id: {}", id);
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return doctorMapper.toDto(doctor);
    }

    @Override
    public DoctorReadDto findByFullName(String lastName, String firstName, String middleName) {
        Doctor doctor = doctorRepository.findByFullName(lastName, firstName, middleName)
                .orElseThrow(NotFoundException::new);
        return doctorMapper.toDto(doctor);
    }

    @Override
    public DoctorReadDto findByPhone(String phone) {
        Doctor doctor = doctorRepository.findByPhone(phone).orElseThrow(NotFoundException::new);
        return doctorMapper.toDto(doctor);
    }

    @Override
    @Transactional
    public DoctorReadDto save(DoctorCreateEditDto doctor) {
        log.debug("saving doctor: {}", doctor);
        Doctor entity = doctorMapper.toEntity(doctor);
        return doctorMapper.toDto(doctorRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, DoctorCreateEditDto updatedDoctor) {
        log.debug("updating doctor with id {}", id);
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        int updated = doctorRepository.updateById(
                id,
                updatedDoctor.lastName(),
                updatedDoctor.firstName(),
                updatedDoctor.middleName(),
                updatedDoctor.specialty(),
                updatedDoctor.phone(),
                updatedDoctor.email(),
                updatedDoctor.information(),
                updatedDoctor.rating(),
                updatedDoctor.avatarPath()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Transactional
    public String updateAvatar(UUID id, MultipartFile avatarFile) throws IOException {
        log.debug("updating avatar for doctor with id {}", id);
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        if (doctor.getAvatarPath() != null) {
            fileStorageService.deleteFile(doctor.getAvatarPath());
        }
        String avatarPath = fileStorageService.storeFile(avatarFile, id);
        int updated = doctorRepository.updateAvatarPath(id, avatarPath);
        if (updated == 0) {
            throw new UpdateException(id);
        }

        return avatarPath;
    }

    @Transactional
    public void deleteAvatar(UUID id) {
        log.debug("deleting avatar for doctor with id {}", id);

        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (doctor.getAvatarPath() != null) {
            fileStorageService.deleteFile(doctor.getAvatarPath());
            doctorRepository.updateAvatarPath(id, null);
        }
    }

    public byte[] getAvatar(UUID id) throws IOException {
        log.debug("getting avatar for doctor with id {}", id);

        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (doctor.getAvatarPath() == null) {
            return null;
        }

        return fileStorageService.loadFile(doctor.getAvatarPath());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting doctor with id: {}", id);
        doctorRepository.deleteById(id);
    }

    public List<DoctorReadDto> findBySpecialty(String specialty, Pageable pageable) {
        return doctorMapper.toDto(doctorRepository.findBySpecialty(specialty, pageable));
    }

    public List<DoctorReadDto> findByRatingGreaterThanEqual(Double minRating, Pageable pageable) {
        return doctorMapper.toDto(doctorRepository.findByRatingGreaterThanEqual(minRating, pageable));
    }
}