package medicalcenter.userservice.mapper;

import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewCreateEditDto;
import medicalcenter.userservice.model.dto.doctorreview.DoctorReviewReadDto;
import medicalcenter.userservice.model.entity.DoctorReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DoctorReviewsMapper {
    
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "visit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DoctorReview toEntity(DoctorReviewCreateEditDto dto);
    
    DoctorReview toEntity(DoctorReviewReadDto dto);
    
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.lastName", target = "patientName")
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(source = "doctor.lastName", target = "doctorName")
    @Mapping(source = "visit.id", target = "visitId")
    @Mapping(source = "visit.dateOfVisit", target = "visitDate")
    DoctorReviewReadDto toDto(DoctorReview doctorReview);
    
    List<DoctorReviewReadDto> toDto(List<DoctorReview> doctorReviews);
}