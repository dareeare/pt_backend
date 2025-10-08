package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.manager.ManagerCreateEditDto;
import medicalcenter.userservice.model.dto.manager.ManagerReadDto;
import medicalcenter.userservice.model.entity.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ManagerMapper {
    Manager toEntity(ManagerCreateEditDto dto);
    
    Manager toEntity(ManagerReadDto dto);
    
    ManagerReadDto toDto(Manager manager);
    
    List<ManagerReadDto> toDto(List<Manager> managers);
}