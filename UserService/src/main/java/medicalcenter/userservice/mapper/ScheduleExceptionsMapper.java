package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.scheduleexception.ScheduleExceptionCreateEditDto;
import medicalcenter.userservice.model.dto.scheduleexception.ScheduleExceptionReadDto;
import medicalcenter.userservice.model.entity.ScheduleException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScheduleExceptionsMapper {
    
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ScheduleException toEntity(ScheduleExceptionCreateEditDto dto);
    
    ScheduleException toEntity(ScheduleExceptionReadDto dto);
    
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.lastName", target = "doctorName")
    ScheduleExceptionReadDto toDto(ScheduleException scheduleExceptions);
    
    List<ScheduleExceptionReadDto> toDto(List<ScheduleException> scheduleExceptions);
}