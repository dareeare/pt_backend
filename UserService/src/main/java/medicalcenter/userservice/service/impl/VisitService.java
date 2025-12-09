package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.VisitMapper;
import medicalcenter.userservice.model.dto.visit.RescheduleVisitDto;
import medicalcenter.userservice.model.dto.visit.VisitCreateEditDto;
import medicalcenter.userservice.model.dto.visit.VisitReadDto;
import medicalcenter.userservice.model.entity.TimeSlot;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.TimeSlotRepository;
import medicalcenter.userservice.repository.VisitRepository;
import medicalcenter.userservice.service.CrudService;
import medicalcenter.userservice.service.impl.TimeSlotsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class VisitService implements CrudService<VisitCreateEditDto, VisitReadDto> {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotsService timeSlotsService;

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

    public List<VisitReadDto> findPastVisitsByPatientId(UUID patientId, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findPastVisitsByPatientId(patientId, LocalDateTime.now(), pageable));
    }

    public List<VisitReadDto> findFutureVisitsByPatientId(UUID patientId, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findFutureVisitsByPatientId(patientId, LocalDateTime.now(), pageable));
    }

    public List<VisitReadDto> findPastVisitsByDoctorId(UUID doctorId, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findPastVisitsByDoctorId(doctorId, LocalDateTime.now(), pageable));
    }

    public List<VisitReadDto> findFutureVisitsByDoctorId(UUID doctorId, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findFutureVisitsByDoctorId(doctorId, LocalDateTime.now(), pageable));
    }

    public List<VisitReadDto> findPastVisitsByDoctorAndPatient(UUID doctorId, UUID patientId, Pageable pageable) {
        return visitMapper.toDto(visitRepository.findPastVisitsByDoctorAndPatient(doctorId, patientId, LocalDateTime.now(), pageable));
    }

    @Transactional
    public void cancelVisit(UUID visitId) {
        log.debug("cancelling visit with id: {}", visitId);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new NotFoundException(visitId));
        
        // Освобождаем слот, если он был зарезервирован
        timeSlotRepository.findByVisitId(visitId)
                .ifPresent(timeSlot -> {
                    timeSlotsService.releaseSlot(timeSlot.getId());
                    log.debug("Slot {} released for cancelled visit {}", timeSlot.getId(), visitId);
                });
        
        visit.setStatus("cancelled");
        visitRepository.save(visit);
        log.info("Visit {} cancelled successfully and slot released", visitId);
    }

    @Transactional
    public VisitReadDto rescheduleVisit(UUID visitId, RescheduleVisitDto rescheduleDto) {
        log.debug("rescheduling visit {} to new date: {}", visitId, rescheduleDto.newDateOfVisit());
        
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new NotFoundException(visitId));
        
        // Проверяем, что визит не завершен
        if ("completed".equals(visit.getStatus())) {
            throw new IllegalArgumentException("Cannot reschedule a completed visit");
        }
        
        // Освобождаем текущий слот, если он был зарезервирован
        timeSlotRepository.findByVisitId(visitId)
                .ifPresent(timeSlot -> {
                    timeSlotsService.releaseSlot(timeSlot.getId());
                    log.debug("Old slot {} released for visit {}", timeSlot.getId(), visitId);
                });
        
        // Находим или создаем новый слот для новой даты/времени
        LocalDate newSlotDate = rescheduleDto.newDateOfVisit().toLocalDate();
        LocalTime newStartTime = rescheduleDto.newDateOfVisit().toLocalTime();
        
        TimeSlot newSlot = timeSlotRepository
                .findByDoctorIdAndSlotDateAndStartTime(rescheduleDto.doctorId(), newSlotDate, newStartTime)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Time slot not found for doctor %s on %s at %s", 
                                rescheduleDto.doctorId(), newSlotDate, newStartTime)));
        
        // Проверяем, что слот свободен
        if (newSlot.getVisit() != null) {
            throw new IllegalArgumentException(
                    String.format("Time slot %s is already booked", newSlot.getId()));
        }
        
        // Обновляем визит с новой датой
        // Врач определяется из нового слота, поэтому если doctorId изменился,
        // это будет обработано через связь визита с новым слотом
        visit.setDateOfVisit(rescheduleDto.newDateOfVisit());
        
        Visit updatedVisit = visitRepository.save(visit);
        
        // Резервируем новый слот
        timeSlotsService.bookSlot(newSlot.getId(), updatedVisit);
        log.info("Visit {} rescheduled to {} and new slot {} booked", 
                visitId, rescheduleDto.newDateOfVisit(), newSlot.getId());
        
        return visitMapper.toDto(updatedVisit);
    }
}