package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.operator.OperatorCreateEditDto;
import medicalcenter.userservice.model.dto.operator.OperatorReadDto;
import medicalcenter.userservice.model.entity.Operator;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperatorMapper {
    Operator toEntity(OperatorCreateEditDto dto);
    
    Operator toEntity(OperatorReadDto dto);
    
    OperatorReadDto toDto(Operator operators);
    
    List<OperatorReadDto> toDto(List<Operator> operators);
}