package medicalcenter.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.exception.NotFoundException;
import medicalcenter.userservice.exception.UpdateException;
import medicalcenter.userservice.mapper.ScheduleExceptionsMapper;
import medicalcenter.userservice.model.dto.scheduleexception.ScheduleExceptionCreateEditDto;
import medicalcenter.userservice.model.dto.scheduleexception.ScheduleExceptionReadDto;
import medicalcenter.userservice.model.entity.ScheduleException;
import medicalcenter.userservice.repository.ScheduleExceptionRepository;
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
public class ScheduleExceptionsService implements CrudService<ScheduleExceptionCreateEditDto, ScheduleExceptionReadDto> {
    private final ScheduleExceptionRepository scheduleExceptionsRepository;
    private final ScheduleExceptionsMapper scheduleExceptionsMapper;

    @Override
    public List<ScheduleExceptionReadDto> findAll(Pageable pageable) {
        log.debug("findAll() method is called from ScheduleExceptionsService");
        return scheduleExceptionsMapper.toDto(scheduleExceptionsRepository.findAll(pageable).getContent());
    }

    @Override
    public List<ScheduleExceptionReadDto> findAllByLastName(String name, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for ScheduleExceptions entity");
    }

    @Override
    public List<ScheduleExceptionReadDto> findAllByLastFirstName(String lastName, String firstName, Pageable pageable) {
        throw new UnsupportedOperationException("Method not supported for ScheduleExceptions entity");
    }

    @Override
    public ScheduleExceptionReadDto findOne(UUID id) {
        log.debug("finding schedule exception with id: {}", id);
        ScheduleException scheduleException = scheduleExceptionsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return scheduleExceptionsMapper.toDto(scheduleException);
    }

    @Override
    public ScheduleExceptionReadDto findByFullName(String lastName, String firstName, String middleName) {
        throw new UnsupportedOperationException("Method not supported for ScheduleExceptions entity");
    }

    @Override
    public ScheduleExceptionReadDto findByPhone(String phone) {
        throw new UnsupportedOperationException("Method not supported for ScheduleExceptions entity");
    }

    @Override
    @Transactional
    public ScheduleExceptionReadDto save(ScheduleExceptionCreateEditDto scheduleException) {
        log.debug("saving schedule exception: {}", scheduleException);
        ScheduleException entity = scheduleExceptionsMapper.toEntity(scheduleException);
        return scheduleExceptionsMapper.toDto(scheduleExceptionsRepository.save(entity));
    }

    @Override
    @Transactional
    public void update(UUID id, ScheduleExceptionCreateEditDto updatedScheduleException) {
        log.debug("updating schedule exception with id {}", id);
        ScheduleException scheduleException = scheduleExceptionsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        int updated = scheduleExceptionsRepository.updateById(
                id,
                updatedScheduleException.doctorId(),
                updatedScheduleException.exceptionDate(),
                updatedScheduleException.reason(),
                updatedScheduleException.isWorkingDay()
        );
        if (updated == 0) {
            throw new UpdateException(id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("deleting schedule exception with id: {}", id);
        scheduleExceptionsRepository.deleteById(id);
    }

    public List<ScheduleExceptionReadDto> findByDoctorId(UUID doctorId, Pageable pageable) {
        return scheduleExceptionsMapper.toDto(scheduleExceptionsRepository.findByDoctorId(doctorId, pageable));
    }

    public List<ScheduleExceptionReadDto> findByExceptionDate(LocalDate exceptionDate, Pageable pageable) {
        return scheduleExceptionsMapper.toDto(scheduleExceptionsRepository.findByExceptionDate(exceptionDate, pageable));
    }

    public ScheduleExceptionReadDto findByDoctorIdAndExceptionDate(UUID doctorId, LocalDate exceptionDate) {
        ScheduleException scheduleException = scheduleExceptionsRepository
                .findByDoctorIdAndExceptionDate(doctorId, exceptionDate)
                .orElseThrow(() -> new NotFoundException("Schedule exception not found"));
        return scheduleExceptionsMapper.toDto(scheduleException);
    }

    public List<ScheduleExceptionReadDto> findByIsWorkingDay(Boolean isWorkingDay, Pageable pageable) {
        return scheduleExceptionsMapper.toDto(scheduleExceptionsRepository.findByIsWorkingDay(isWorkingDay, pageable));
    }
}