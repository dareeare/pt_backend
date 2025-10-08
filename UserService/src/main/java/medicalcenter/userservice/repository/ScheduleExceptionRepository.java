package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.ScheduleException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, UUID> {

    @Query("SELECT se FROM ScheduleException se WHERE se.doctor.id = :doctorId")
    List<ScheduleException> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT se FROM ScheduleException se WHERE se.exceptionDate = :exceptionDate")
    List<ScheduleException> findByExceptionDate(@Param("exceptionDate") LocalDate exceptionDate, Pageable pageable);

    @Query("SELECT se FROM ScheduleException se WHERE se.doctor.id = :doctorId AND se.exceptionDate = :exceptionDate")
    Optional<ScheduleException> findByDoctorIdAndExceptionDate(@Param("doctorId") UUID doctorId,
                                                               @Param("exceptionDate") LocalDate exceptionDate);

    @Query("SELECT se FROM ScheduleException se WHERE se.isWorkingDay = :isWorkingDay")
    List<ScheduleException> findByIsWorkingDay(@Param("isWorkingDay") Boolean isWorkingDay, Pageable pageable);

    @Query("SELECT se FROM ScheduleException se WHERE se.exceptionDate BETWEEN :startDate AND :endDate")
    List<ScheduleException> findByExceptionDateBetween(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE ScheduleException se
            SET se.doctor.id = :doctorId,
                se.exceptionDate = :exceptionDate,
                se.reason = :reason,
                se.isWorkingDay = :isWorkingDay
            WHERE se.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                  @Param("doctorId") UUID doctorId,
                  @Param("exceptionDate") LocalDate exceptionDate,
                  @Param("reason") String reason,
                  @Param("isWorkingDay") Boolean isWorkingDay);
}