package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.service.ServiceCreateEditDto;
import medicalcenter.userservice.model.dto.service.ServiceReadDto;
import medicalcenter.userservice.model.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceMapper {
    
    @Mapping(target = "doctor", ignore = true)
    Service toEntity(ServiceCreateEditDto dto);
    
    Service toEntity(ServiceReadDto dto);
    
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.lastName", target = "doctorName")
    ServiceReadDto toDto(Service service);
    
    List<ServiceReadDto> toDto(List<Service> services);
}