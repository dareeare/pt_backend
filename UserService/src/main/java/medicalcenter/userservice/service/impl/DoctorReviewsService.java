package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.DoctorReviewsMapper;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewCreateEditDto;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewReadDto;
import medicalcenter.userservice.model.entity.DoctorReview;
import medicalcenter.userservice.repository.DoctorReviewRepository;
import medicalcenter.userservice.service.CrudService;
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
    public DoctorReviewReadDto save(DoctorReviewCreateEditDto doctorReview) {
        log.debug("saving doctor review: {}", doctorReview);
        DoctorReview entity = doctorReviewsMapper.toEntity(doctorReview);
        return doctorReviewsMapper.toDto(doctorReviewsRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, DoctorReviewCreateEditDto updatedDoctorReview) {
        log.debug("updating doctor review with id {}", id);
        DoctorReview doctorReview = doctorReviewsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
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
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting doctor review with id: {}", id);
        doctorReviewsRepository.deleteById(id);
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
        doctorReview.setIsApproved(true);
        doctorReviewsRepository.save(doctorReview);
    }

    public Double getAverageRatingByDoctorId(UUID doctorId) {
        return doctorReviewsRepository.findAverageRatingByDoctorId(doctorId);
    }

    public Long getApprovedReviewsCountByDoctorId(UUID doctorId) {
        return doctorReviewsRepository.countApprovedReviewsByDoctorId(doctorId);
    }
}