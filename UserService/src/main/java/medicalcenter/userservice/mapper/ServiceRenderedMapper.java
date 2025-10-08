package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.servicerendered.ServiceRenderedCreateEditDto;
import medicalcenter.userservice.model.dto.servicerendered.ServiceRenderedReadDto;
import medicalcenter.userservice.model.entity.ServiceRendered;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceRenderedMapper {
    
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "service", ignore = true)
    ServiceRendered toEntity(ServiceRenderedCreateEditDto dto);
    
    ServiceRendered toEntity(ServiceRenderedReadDto dto);
    
    @Mapping(source = "visit.id", target = "visitId")
    @Mapping(source = "visit.dateOfVisit", target = "visitDate")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.nameOfService", target = "serviceName")
    @Mapping(source = "service.cost", target = "standardCost")
    ServiceRenderedReadDto toDto(ServiceRendered serviceRendered);
    
    List<ServiceRenderedReadDto> toDto(List<ServiceRendered> serviceRenderedList);
}