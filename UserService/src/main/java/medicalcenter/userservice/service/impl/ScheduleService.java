package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.ScheduleMapper;
import medicalcenter.userservice.model.dto.schedule.ScheduleCreateEditDto;
import medicalcenter.userservice.model.dto.schedule.ScheduleReadDto;
import medicalcenter.userservice.model.entity.Schedule;
import medicalcenter.userservice.repository.ScheduleRepository;
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
@Slf4j
public class ScheduleService implements CrudService<ScheduleCreateEditDto, ScheduleReadDto> {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    public List<ScheduleReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from ScheduleService");
        return scheduleMapper.toDto(scheduleRepository.findAll(pageable).getContent());
    }

    @Override
    public List<ScheduleReadDto> findAllByLastName(String name, Pageable pageable) {
        return scheduleMapper.toDto(scheduleRepository.findByDoctorLastNameContainingIgnoreCase(name, pageable));
    }

    @Override
    public List<ScheduleReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        return scheduleMapper.toDto(scheduleRepository.findByDoctorLastNameAndDoctorFirstName(
                lastName, firstName, pageable));
    }

    @Override
    public ScheduleReadDto findOne(UUID id) {
        log.debug("finding schedule with id: {}", id);
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        return scheduleMapper.toDto(schedule);
    }

    @Override
    public ScheduleReadDto findByFullName(String lastName, String firstName, String middleName) {
        throw new UnsupportedOperationException("Method not supported for Schedule entity");
    }

    @Override
    public ScheduleReadDto findByPhone(String phone) {
        throw new UnsupportedOperationException("Method not supported for Schedule entity");
    }

    @Override
    @Transactional
    public ScheduleReadDto save(ScheduleCreateEditDto schedule) {
        log.debug("saving schedule: {}", schedule);
        Schedule entity = scheduleMapper.toEntity(schedule);
        return scheduleMapper.toDto(scheduleRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, ScheduleCreateEditDto updatedSchedule) {
        log.debug("updating schedule with id {}", id);
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        int updated = scheduleRepository.updateById(
                id,
                updatedSchedule.startTime(),
                updatedSchedule.endTime(),
                updatedSchedule.workDay(),
                updatedSchedule.doctorId()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting schedule with id: {}", id);
        scheduleRepository.deleteById(id);
    }

    public List<ScheduleReadDto> findByDoctorId(UUID doctorId, Pageable pageable) {
        return scheduleMapper.toDto(scheduleRepository.findByDoctorId(doctorId, pageable));
    }

    public List<ScheduleReadDto> findByWorkDay(LocalDate workDay, Pageable pageable) {
        return scheduleMapper.toDto(scheduleRepository.findByWorkDay(workDay, pageable));
    }

    public List<ScheduleReadDto> findByDoctorIdAndWorkDay(UUID doctorId, LocalDate workDay, Pageable pageable) {
        return scheduleMapper.toDto(scheduleRepository.findByDoctorIdAndWorkDay(doctorId, workDay, pageable));
    }
}