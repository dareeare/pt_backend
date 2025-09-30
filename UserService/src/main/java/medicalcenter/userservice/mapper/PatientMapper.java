package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.PatientCreateEditDto;
import medicalcenter.userservice.model.dto.PatientReadDto;
import medicalcenter.userservice.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatientMapper {
    Patient toEntity(PatientCreateEditDto dto);

    Patient toEntity(PatientReadDto dto);

    PatientReadDto toDto(Patient patient);

    List<PatientReadDto> toDto(List<Patient> patients);
}
