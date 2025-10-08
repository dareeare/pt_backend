package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.TimeSlotsMapper;
import medicalcenter.userservice.model.dto.timeslot.TimeSlotCreateEditDto;
import medicalcenter.userservice.model.dto.timeslot.TimeSlotReadDto;
import medicalcenter.userservice.model.entity.TimeSlot;
import medicalcenter.userservice.model.entity.Visit;
import medicalcenter.userservice.repository.TimeSlotRepository;
import medicalcenter.userservice.service.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class TimeSlotsService implements CrudService<TimeSlotCreateEditDto, TimeSlotReadDto> {
    private final TimeSlotRepository timeSlotsRepository;
    private final TimeSlotsMapper timeSlotsMapper;

    @Override
    public List<TimeSlotReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from TimeSlotsService");
        return timeSlotsMapper.toDto(timeSlotsRepository.findAll(pageable).getContent());
    }

    @Override
    public List<TimeSlotReadDto> findAllByLastName(String name, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for TimeSlots entity");
    }

    @Override
    public List<TimeSlotReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for TimeSlots entity");
    }

    @Override
    public TimeSlotReadDto findOne(UUID id) {
        log.debug("finding time slot with id: {}", id);
        TimeSlot timeSlot = timeSlotsRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return timeSlotsMapper.toDto(timeSlot);
    }

    @Override
    public TimeSlotReadDto findByFullName(String lastName, String firstName, String middleName) {
        throw new UnsupportedOperationException("Method not supported for TimeSlots entity");
    }

    @Override
    public TimeSlotReadDto findByPhone(String phone) {
        throw new UnsupportedOperationException("Method not supported for TimeSlots entity");
    }

    @Override
    @Transactional
    public TimeSlotReadDto save(TimeSlotCreateEditDto timeSlot) {
        log.debug("saving time slot: {}", timeSlot);
        TimeSlot entity = timeSlotsMapper.toEntity(timeSlot);
        return timeSlotsMapper.toDto(timeSlotsRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, TimeSlotCreateEditDto updatedTimeSlot) {
        log.debug("updating time slot with id {}", id);
        TimeSlot timeSlot = timeSlotsRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        int updated = timeSlotsRepository.updateById(
                id,
                updatedTimeSlot.doctorId(),
                updatedTimeSlot.slotDate(),
                updatedTimeSlot.startTime(),
                updatedTimeSlot.endTime(),
                updatedTimeSlot.visit()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting time slot with id: {}", id);
        timeSlotsRepository.deleteById(id);
    }

    public List<TimeSlotReadDto> findByDoctorId(UUID doctorId, Pageable pageable) {
        return timeSlotsMapper.toDto(timeSlotsRepository.findByDoctorId(doctorId, pageable));
    }

    public List<TimeSlotReadDto> findBySlotDate(LocalDate slotDate, Pageable pageable) {
        return timeSlotsMapper.toDto(timeSlotsRepository.findBySlotDate(slotDate, pageable));
    }

    public List<TimeSlotReadDto> findByDoctorIdAndSlotDate(UUID doctorId, LocalDate slotDate, Pageable pageable) {
        return timeSlotsMapper.toDto(timeSlotsRepository.findByDoctorIdAndSlotDate(doctorId, slotDate, pageable));
    }

    public List<TimeSlotReadDto> findAvailableSlots(Pageable pageable) {
        return timeSlotsMapper.toDto(timeSlotsRepository.findAvailableSlots(pageable));
    }

    public List<TimeSlotReadDto> findAvailableSlotsByDoctorId(UUID doctorId, Pageable pageable) {
        return timeSlotsMapper.toDto(timeSlotsRepository.findAvailableSlotsByDoctorId(doctorId, pageable));
    }

    public List<TimeSlotReadDto> findAvailableSlotsByDoctorIdAndDate(UUID doctorId, LocalDate slotDate, Pageable pageable) {
        return timeSlotsMapper.toDto(timeSlotsRepository.findAvailableSlotsByDoctorIdAndDate(doctorId, slotDate, pageable));
    }

    @Transactional
    public TimeSlotReadDto bookSlot(UUID slotId, Visit visit) {
        log.debug("booking slot {} for visit {}", slotId, visit);
        TimeSlot timeSlot = timeSlotsRepository.findById(slotId).orElseThrow(() -> new NotFoundException(slotId));
        timeSlot.setVisit(visit);
        return timeSlotsMapper.toDto(timeSlotsRepository.save(timeSlot));
    }

    @Transactional
    public TimeSlotReadDto releaseSlot(UUID slotId) {
        log.debug("releasing slot {}", slotId);
        TimeSlot timeSlot = timeSlotsRepository.findById(slotId).orElseThrow(() -> new NotFoundException(slotId));
        timeSlot.setVisit(null);
        return timeSlotsMapper.toDto(timeSlotsRepository.save(timeSlot));
    }
}