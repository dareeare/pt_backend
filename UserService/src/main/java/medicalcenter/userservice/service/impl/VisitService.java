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
import medicalcenter.userservice.repository.DoctorRepository;
import medicalcenter.userservice.repository.TimeSlotRepository;
import medicalcenter.userservice.repository.VisitRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class VisitService implements CrudService<VisitCreateEditDto, VisitReadDto> {
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
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
        visitRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
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

    /**
     * Отменяет запись - устанавливает статус "cancelled" и освобождает связанный слот времени
     */
    @Transactional
    public VisitReadDto cancel(UUID visitId) {
        log.debug("cancelling visit with id {}", visitId);
        Visit visit = visitRepository.findById(visitId).orElseThrow(() -> new NotFoundException(visitId));

        // Проверяем, что запись еще не отменена и не завершена
        if ("cancelled".equals(visit.getStatus())) {
            log.warn("Visit {} is already cancelled", visitId);
            return visitMapper.toDto(visit);
        }

        if ("completed".equals(visit.getStatus())) {
            throw new IllegalStateException("Cannot cancel a completed visit");
        }

        // Обновляем статус записи на "cancelled" используя метод update
        VisitCreateEditDto updateDto = new VisitCreateEditDto(
                visit.getDateOfVisit(),
                visit.getDoctor().getId(),
                visit.getPatient().getId(),
                "cancelled",
                visit.getSymptoms(),
                visit.getDiagnosis(), // diagnosis не может быть пустым, оставляем текущее значение
                visit.getPrescription()
        );
        update(visitId, updateDto);

        // Освобождаем связанный слот времени, если он существует
        Optional<TimeSlot> timeSlotOpt = timeSlotRepository.findByVisitId(visitId);
        if (timeSlotOpt.isPresent()) {
            TimeSlot timeSlot = timeSlotOpt.get();
            timeSlotsService.releaseSlot(timeSlot.getId());
            log.debug("Released time slot {} for cancelled visit {}", timeSlot.getId(), visitId);
        }

        return visitMapper.toDto(visitRepository.findById(visitId).orElseThrow(() -> new NotFoundException(visitId)));
    }

    /**
     * Переносит запись на другое время/дату - обновляет дату, освобождает старый слот и резервирует новый
     */
    @Transactional
    public VisitReadDto reschedule(UUID visitId, RescheduleVisitDto rescheduleDto) {
        log.debug("rescheduling visit {} to new date {}", visitId, rescheduleDto.newDateOfVisit());
        Visit visit = visitRepository.findById(visitId).orElseThrow(() -> new NotFoundException(visitId));

        // Проверяем, что запись еще не отменена и не завершена
        if ("cancelled".equals(visit.getStatus())) {
            throw new IllegalStateException("Cannot reschedule a cancelled visit");
        }

        if ("completed".equals(visit.getStatus())) {
            throw new IllegalStateException("Cannot reschedule a completed visit");
        }

        // Проверяем, что врач существует
        doctorRepository.findById(rescheduleDto.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found with id: " + rescheduleDto.doctorId()));

        // Освобождаем старый слот времени, если он существует
        Optional<TimeSlot> oldTimeSlotOpt = timeSlotRepository.findByVisitId(visitId);
        if (oldTimeSlotOpt.isPresent()) {
            TimeSlot oldTimeSlot = oldTimeSlotOpt.get();
            timeSlotsService.releaseSlot(oldTimeSlot.getId());
            log.debug("Released old time slot {} for rescheduled visit {}", oldTimeSlot.getId(), visitId);
        }

        // Находим или используем указанный слот времени
        TimeSlot newTimeSlot;
        if (rescheduleDto.timeSlotId() != null) {
            // Используем указанный слот
            newTimeSlot = timeSlotRepository.findById(rescheduleDto.timeSlotId())
                    .orElseThrow(() -> new NotFoundException("TimeSlot not found with id: " + rescheduleDto.timeSlotId()));
            
            // Проверяем, что слот свободен
            if (newTimeSlot.getVisit() != null) {
                throw new IllegalStateException("TimeSlot is already booked");
            }

            // Проверяем, что слот соответствует врачу и времени
            if (!newTimeSlot.getDoctor().getId().equals(rescheduleDto.doctorId())) {
                throw new IllegalStateException("TimeSlot does not match the specified doctor");
            }
        } else {
            // Ищем слот по врачу, дате и времени
            LocalDate slotDate = rescheduleDto.newDateOfVisit().toLocalDate();
            LocalTime startTime = rescheduleDto.newDateOfVisit().toLocalTime();
            
            Optional<TimeSlot> slotOpt = timeSlotRepository.findByDoctorIdAndSlotDateAndStartTime(
                    rescheduleDto.doctorId(), slotDate, startTime);
            
            if (slotOpt.isEmpty()) {
                throw new NotFoundException("No time slot found for doctor " + rescheduleDto.doctorId() + 
                        " on date " + slotDate + " at time " + startTime);
            }

            newTimeSlot = slotOpt.get();
            
            // Проверяем, что слот свободен
            if (newTimeSlot.getVisit() != null) {
                throw new IllegalStateException("TimeSlot is already booked");
            }
        }

        // Обновляем запись с новыми данными используя метод update
        VisitCreateEditDto updateDto = new VisitCreateEditDto(
                rescheduleDto.newDateOfVisit(),
                rescheduleDto.doctorId(),
                visit.getPatient().getId(),
                "scheduled", // при переносе запись остается в статусе scheduled
                visit.getSymptoms(),
                visit.getDiagnosis(),
                visit.getPrescription()
        );
        update(visitId, updateDto);

        // Обновляем Visit для связи с новым слотом
        visit = visitRepository.findById(visitId).orElseThrow(() -> new NotFoundException(visitId));
        
        // Резервируем новый слот
        timeSlotsService.bookSlot(newTimeSlot.getId(), visit);
        log.debug("Booked new time slot {} for rescheduled visit {}", newTimeSlot.getId(), visitId);

        return visitMapper.toDto(visit);
    }
}