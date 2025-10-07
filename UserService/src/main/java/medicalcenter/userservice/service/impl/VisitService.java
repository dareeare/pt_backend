package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.VisitMapper;
import medicalcenter.userservice.model.dto.visit.VisitCreateEditDto;
import medicalcenter.userservice.model.dto.visit.VisitReadDto;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.VisitRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class VisitService implements CrudService<VisitCreateEditDto, VisitReadDto> {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;

    @Override
    public List<VisitReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from VisitService");
        return visitMapper.toDto(visitRepository.findAll(pageable).getContent());
    }

    @Override
    public List<VisitReadDto> findAllByLastName(String name, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findByPatientLastNameContainingIgnoreCase(name, pageable));
    }

    @Override
    public List<VisitReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findByPatientLastNameAndPatientFirstName(
                lastName, firstName, pageable));
    }

    @Override
    public VisitReadDto findOne(UUID id) {
        log.debug("finding visit with id: {}", id);
        Visit visit = visitRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return visitMapper.toDto(visit);
    }

    @Override
    public VisitReadDto findByFullName(String lastName, String firstName, String middleName) {
        throw new UnsupportedOperationException("Method not supported for Visit entity");
    }

    @Override
    public VisitReadDto findByPhone(String phone) {
        throw new UnsupportedOperationException("Method not supported for Visit entity");
    }

    @Override
    @Transactional
    public VisitReadDto save(VisitCreateEditDto visit) {
        log.debug("saving visit: {}", visit);
        Visit entity = visitMapper.toEntity(visit);
        return visitMapper.toDto(visitRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, VisitCreateEditDto updatedVisit) {
        log.debug("updating visit with id {}", id);
        Visit visit = visitRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        int updated = visitRepository.updateById(
                id,
                updatedVisit.dateOfVisit(),
                updatedVisit.doctorId(),
                updatedVisit.patientId(),
                updatedVisit.status(),
                updatedVisit.symptoms(),
                updatedVisit.diagnosis(),
                updatedVisit.prescription()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting visit with id: {}", id);
        visitRepository.deleteById(id);
    }

    public List<VisitReadDto> findByDoctorId(UUID doctorId, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findByDoctorId(doctorId, pageable));
    }

    public List<VisitReadDto> findByPatientId(UUID patientId, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findByPatientId(patientId, pageable));
    }

    public List<VisitReadDto> findByStatus(String status, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findByStatus(status, pageable));
    }
}