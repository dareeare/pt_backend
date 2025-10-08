package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.schedule.ScheduleCreateEditDto;
import medicalcenter.userservice.model.dto.schedule.ScheduleReadDto;
import medicalcenter.userservice.model.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScheduleMapper {
    
    @Mapping(target = "doctor", ignore = true)
    Schedule toEntity(ScheduleCreateEditDto dto);
    
    Schedule toEntity(ScheduleReadDto dto);
    
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.lastName", target = "doctorName")
    ScheduleReadDto toDto(Schedule schedule);
    
    List<ScheduleReadDto> toDto(List<Schedule> schedules);
}