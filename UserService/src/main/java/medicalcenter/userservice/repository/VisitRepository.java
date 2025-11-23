package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Visit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VisitRepository extends JpaRepository<Visit, UUID> {

    @Query("SELECT v FROM Visit v WHERE LOWER(v.patient.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Visit> findByPatientLastNameContainingIgnoreCase(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.patient.lastName = :lastName AND v.patient.firstName = :firstName")
    List<Visit> findByPatientLastNameAndPatientFirstName(@Param("lastName") String lastName,
                                                         @Param("firstName") String firstName,
                                                         Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId")
    List<Visit> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.patient.id = :patientId")
    List<Visit> findByPatientId(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.status = :status")
    List<Visit> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.dateOfVisit BETWEEN :startDate AND :endDate")
    List<Visit> findByDateOfVisitBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.patient.id = :patientId AND v.dateOfVisit < :currentDate ORDER BY v.dateOfVisit DESC")
    List<Visit> findPastVisitsByPatientId(@Param("patientId") UUID patientId,
                                          @Param("currentDate") LocalDateTime currentDate,
                                          Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.patient.id = :patientId AND v.dateOfVisit >= :currentDate ORDER BY v.dateOfVisit ASC")
    List<Visit> findFutureVisitsByPatientId(@Param("patientId") UUID patientId,
                                            @Param("currentDate") LocalDateTime currentDate,
                                            Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.dateOfVisit < :currentDate ORDER BY v.dateOfVisit DESC")
    List<Visit> findPastVisitsByDoctorId(@Param("doctorId") UUID doctorId,
                                         @Param("currentDate") LocalDateTime currentDate,
                                         Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.dateOfVisit >= :currentDate ORDER BY v.dateOfVisit ASC")
    List<Visit> findFutureVisitsByDoctorId(@Param("doctorId") UUID doctorId,
                                           @Param("currentDate") LocalDateTime currentDate,
                                           Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.patient.id = :patientId AND v.dateOfVisit < :currentDate ORDER BY v.dateOfVisit DESC")
    List<Visit> findPastVisitsByDoctorAndPatient(@Param("doctorId") UUID doctorId,
                                                 @Param("patientId") UUID patientId,
                                                 @Param("currentDate") LocalDateTime currentDate,
                                                 Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.patient.id = :patientId AND v.dateOfVisit < :currentDate ORDER BY v.dateOfVisit DESC")
    List<Visit> findPastVisitsByPatientId(@Param("patientId") UUID patientId, 
                                         @Param("currentDate") LocalDateTime currentDate,
                                         Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.patient.id = :patientId AND v.dateOfVisit >= :currentDate ORDER BY v.dateOfVisit ASC")
    List<Visit> findFutureVisitsByPatientId(@Param("patientId") UUID patientId, 
                                           @Param("currentDate") LocalDateTime currentDate,
                                           Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.dateOfVisit < :currentDate ORDER BY v.dateOfVisit DESC")
    List<Visit> findPastVisitsByDoctorId(@Param("doctorId") UUID doctorId, 
                                        @Param("currentDate") LocalDateTime currentDate,
                                        Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.dateOfVisit >= :currentDate ORDER BY v.dateOfVisit ASC")
    List<Visit> findFutureVisitsByDoctorId(@Param("doctorId") UUID doctorId, 
                                          @Param("currentDate") LocalDateTime currentDate,
                                          Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.patient.id = :patientId AND v.dateOfVisit < :currentDate ORDER BY v.dateOfVisit DESC")
    List<Visit> findPastVisitsByDoctorAndPatient(@Param("doctorId") UUID doctorId,
                                                 @Param("patientId") UUID patientId,
                                                 @Param("currentDate") LocalDateTime currentDate,
                                                 Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Visit v
            SET v.dateOfVisit = :dateOfVisit,
                v.doctor.id = :doctorId,
                v.patient.id = :patientId,
                v.status = :status,
                v.symptoms = :symptoms,
                v.diagnosis = :diagnosis,
                v.prescription = :prescription
            WHERE v.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                   @Param("dateOfVisit") LocalDateTime dateOfVisit,
                   @Param("doctorId") UUID doctorId,
                   @Param("patientId") UUID patientId,
                   @Param("status") String status,
                   @Param("symptoms") String symptoms,
                   @Param("diagnosis") String diagnosis,
                   @Param("prescription") String prescription);
}