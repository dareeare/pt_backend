package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.visit.VisitCreateEditDto;
import medicalcenter.userservice.model.dto.visit.VisitReadDto;
import medicalcenter.userservice.model.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VisitMapper {
    
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "servicesRendered", ignore = true)
    @Mapping(target = "review", ignore = true)
    @Mapping(target = "timeSlot", ignore = true)
    Visit toEntity(VisitCreateEditDto dto);
    
    Visit toEntity(VisitReadDto dto);
    
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.lastName", target = "doctorName")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.lastName", target = "patientName")
    VisitReadDto toDto(Visit visit);
    
    List<VisitReadDto> toDto(List<Visit> visits);
}