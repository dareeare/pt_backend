package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.DoctorReviewsMapper;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewCreateEditDto;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewReadDto;
import medicalcenter.userservice.model.entity.DoctorReview;
import medicalcenter.userservice.model.entity.Doctor;
import medicalcenter.userservice.model.entity.Patient;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.DoctorReviewRepository;
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.PatientRepository;
import medicalcenter.userservice.repository.VisitRepository;
import medicalcenter.userservice.service.CrudService;
import medicalcenter.userservice.service.impl.DoctorService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DoctorReviewsService implements CrudService<DoctorReviewCreateEditDto, DoctorReviewReadDto> {
    private final DoctorReviewRepository doctorReviewsRepository;
    private final DoctorReviewsMapper doctorReviewsMapper;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final DoctorService doctorService;

    @Override
    public List<DoctorReviewReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from DoctorReviewsService");
        return doctorReviewsMapper.toDto(doctorReviewsRepository.findAll(pageable).getContent());
    }

    @Override
    public List<DoctorReviewReadDto> findAllByLastName(String name, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for DoctorReviews entity");
    }

    @Override
    public List<DoctorReviewReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for DoctorReviews entity");
    }

    @Override
    public DoctorReviewReadDto findOne(UUID id) {
        log.debug("finding doctor review with id: {}", id);
        DoctorReview doctorReview = doctorReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return doctorReviewsMapper.toDto(doctorReview);
    }

    @Override
    public DoctorReviewReadDto findByFullName(String lastName, String firstName, String middleName) {
        throw new UnsupportedOperationException("Method not supported for DoctorReviews entity");
    }

    @Override
    public DoctorReviewReadDto findByPhone(String phone) {
        throw new UnsupportedOperationException("Method not supported for DoctorReviews entity");
    }

    @Override
    @Transactional
    public DoctorReviewReadDto save(DoctorReviewCreateEditDto doctorReviewDto) {
        log.debug("saving doctor review: {}", doctorReviewDto);
        
        // Загружаем связанные сущности
        Doctor doctor = doctorRepository.findById(doctorReviewDto.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found with id: " + doctorReviewDto.doctorId()));
        
        Patient patient = patientRepository.findById(doctorReviewDto.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found with id: " + doctorReviewDto.patientId()));
        
        Visit visit = visitRepository.findById(doctorReviewDto.visitId())
                .orElseThrow(() -> new NotFoundException("Visit not found with id: " + doctorReviewDto.visitId()));
        
        // Проверяем, что визит принадлежит указанному врачу и пациенту
        if (!visit.getDoctor().getId().equals(doctorReviewDto.doctorId())) {
            throw new IllegalArgumentException("Visit does not belong to the specified doctor");
        }
        if (!visit.getPatient().getId().equals(doctorReviewDto.patientId())) {
            throw new IllegalArgumentException("Visit does not belong to the specified patient");
        }
        
        // Проверяем, что для этого визита еще нет отзыва
        doctorReviewsRepository.findByVisitId(doctorReviewDto.visitId())
                .ifPresent(existingReview -> {
                    throw new IllegalArgumentException("Review already exists for visit id: " + doctorReviewDto.visitId());
                });
        
        // Создаем сущность отзыва
        DoctorReview entity = doctorReviewsMapper.toEntity(doctorReviewDto);
        entity.setDoctor(doctor);
        entity.setPatient(patient);
        entity.setVisit(visit);
        
        // Сохраняем отзыв
        DoctorReview savedReview = doctorReviewsRepository.save(entity);
        
        // Если отзыв сразу одобрен, пересчитываем рейтинг врача
        if (Boolean.TRUE.equals(savedReview.getIsApproved())) {
            doctorService.recalculateRating(doctorReviewDto.doctorId());
        }
        
        return doctorReviewsMapper.toDto(savedReview);
    }

    @Override
    @Transactional
    public void update(UUID id, DoctorReviewCreateEditDto updatedDoctorReview) {
        log.debug("updating doctor review with id {}", id);
        DoctorReview existingReview = doctorReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        
        boolean wasApproved = Boolean.TRUE.equals(existingReview.getIsApproved());
        UUID doctorId = existingReview.getDoctor().getId();
        
        int updated = doctorReviewsRepository.updateById(
                id,
                updatedDoctorReview.patientId(),
                updatedDoctorReview.doctorId(),
                updatedDoctorReview.visitId(),
                updatedDoctorReview.rating(),
                updatedDoctorReview.comment(),
                updatedDoctorReview.isApproved(),
                true // mark as edited
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
        
        // Пересчитываем рейтинг врача, если изменился статус одобрения
        boolean isNowApproved = Boolean.TRUE.equals(updatedDoctorReview.isApproved());
        if (wasApproved != isNowApproved || (isNowApproved && !wasApproved)) {
            doctorService.recalculateRating(doctorId);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting doctor review with id: {}", id);
        DoctorReview doctorReview = doctorReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        
        UUID doctorId = doctorReview.getDoctor().getId();
        boolean wasApproved = Boolean.TRUE.equals(doctorReview.getIsApproved());
        
        doctorReviewsRepository.deleteById(id);
        
        // Пересчитываем рейтинг врача, если удаляемый отзыв был одобрен
        if (wasApproved) {
            doctorService.recalculateRating(doctorId);
        }
    }

    public List<DoctorReviewReadDto> findByDoctorId(UUID doctorId, Pageable pageable) {
        return doctorReviewsMapper.toDto(doctorReviewsRepository.findByDoctorId(doctorId, pageable));
    }

    public List<DoctorReviewReadDto> findByPatientId(UUID patientId, Pageable pageable) {
        return doctorReviewsMapper.toDto(doctorReviewsRepository.findByPatientId(patientId, pageable));
    }

    public DoctorReviewReadDto findByVisitId(UUID visitId) {
        DoctorReview doctorReview = doctorReviewsRepository.findByVisitId(visitId)
                .orElseThrow(() -> new NotFoundException("Review not found for visit id: " + visitId));
        return doctorReviewsMapper.toDto(doctorReview);
    }

    public List<DoctorReviewReadDto> findApprovedByDoctorId(UUID doctorId, Pageable pageable) {
        return doctorReviewsMapper.toDto(doctorReviewsRepository.findApprovedByDoctorId(doctorId, pageable));
    }

    @Transactional
    public void approveReview(UUID id) {
        log.debug("approving review with id: {}", id);
        DoctorReview doctorReview = doctorReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        
        boolean wasApproved = Boolean.TRUE.equals(doctorReview.getIsApproved());
        doctorReview.setIsApproved(true);
        doctorReviewsRepository.save(doctorReview);
        
        // Пересчитываем рейтинг врача, если отзыв был одобрен впервые
        if (!wasApproved) {
            doctorService.recalculateRating(doctorReview.getDoctor().getId());
        }
    }

    public Double getAverageRatingByDoctorId(UUID doctorId) {
        return doctorReviewsRepository.findAverageRatingByDoctorId(doctorId);
    }

    public Long getApprovedReviewsCountByDoctorId(UUID doctorId) {
        return doctorReviewsRepository.countApprovedReviewsByDoctorId(doctorId);
    }
}