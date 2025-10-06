package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.doctor.DoctorCreateEditDto;
import medicalcenter.userservice.model.dto.doctor.DoctorReadDto;
import medicalcenter.userservice.model.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DoctorMapper {
    Doctor toEntity(DoctorCreateEditDto dto);
    
    Doctor toEntity(DoctorReadDto dto);
    
    DoctorReadDto toDto(Doctor doctor);
    
    List<DoctorReadDto> toDto(List<Doctor> doctors);
}