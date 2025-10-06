package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.timeslot.TimeSlotCreateEditDto;
import medicalcenter.userservice.model.dto.timeslot.TimeSlotReadDto;
import medicalcenter.userservice.model.entity.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TimeSlotsMapper {
    
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "visit", ignore = true)
    TimeSlot toEntity(TimeSlotCreateEditDto dto);
    
    TimeSlot toEntity(TimeSlotReadDto dto);
    
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.lastName", target = "doctorName")
    @Mapping(source = "visit.patient.lastName", target = "patientName")
    @Mapping(target = "isAvailable", expression = "java(timeSlots.getVisit() == null)")
    TimeSlotReadDto toDto(TimeSlot timeSlots);
    
    List<TimeSlotReadDto> toDto(List<TimeSlot> timeSlots);
}