package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.DoctorReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorReviewRepository extends JpaRepository<DoctorReview, UUID> {

    @Query("SELECT dr FROM DoctorReview dr WHERE dr.doctor.id = :doctorId")
    List<DoctorReview> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT dr FROM DoctorReview dr WHERE dr.patient.id = :patientId")
    List<DoctorReview> findByPatientId(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT dr FROM DoctorReview dr WHERE dr.visit.id = :visitId")
    Optional<DoctorReview> findByVisitId(@Param("visitId") UUID visitId);

    @Query("SELECT dr FROM DoctorReview dr WHERE dr.doctor.id = :doctorId AND dr.isApproved = true")
    List<DoctorReview> findApprovedByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT AVG(dr.rating) FROM DoctorReview dr WHERE dr.doctor.id = :doctorId AND dr.isApproved = true")
    Double findAverageRatingByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT COUNT(dr) FROM DoctorReviews dr WHERE dr.doctor.id = :doctorId AND dr.isApproved = true")
    Long countApprovedReviewsByDoctorId(@Param("doctorId") UUID doctorId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE DoctorReview dr
            SET dr.patient.id = :patientId,
                dr.doctor.id = :doctorId,
                dr.visit.id = :visitId,
                dr.rating = :rating,
                dr.comment = :comment,
                dr.isApproved = :isApproved,
                dr.isEdited = :isEdited
            WHERE dr.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                  @Param("patientId") UUID patientId,
                  @Param("doctorId") UUID doctorId,
                  @Param("visitId") UUID visitId,
                  @Param("rating") Integer rating,
                  @Param("comment") String comment,
                  @Param("isApproved") Boolean isApproved,
                  @Param("isEdited") Boolean isEdited);
}