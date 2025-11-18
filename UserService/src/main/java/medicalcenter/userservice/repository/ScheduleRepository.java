package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    @Query("SELECT s FROM Schedule s WHERE LOWER(s.doctor.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Schedule> findByDoctorLastNameContainingIgnoreCase(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.lastName = :lastName AND s.doctor.firstName = :firstName")
    List<Schedule> findByDoctorLastNameAndDoctorFirstName(@Param("lastName") String lastName,
                                                          @Param("firstName") String firstName,
                                                          Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId")
    List<Schedule> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.workDay = :workDay")
    List<Schedule> findByWorkDay(@Param("workDay") LocalDate workDay, Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.workDay = :workDay")
    List<Schedule> findByDoctorIdAndWorkDay(@Param("doctorId") UUID doctorId,
                                            @Param("workDay") LocalDate workDay,
                                            Pageable pageable);

    @Query("SELECT s FROM Schedule s WHERE s.workDay BETWEEN :startDate AND :endDate")
    List<Schedule> findByWorkDayBetween(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Schedule s
            SET s.startTime = :startTime,
                s.endTime = :endTime,
                s.workDay = :workDay,
                s.doctor.id = :doctorId
            WHERE s.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                   @Param("startTime") LocalDateTime startTime,
                   @Param("endTime") LocalDateTime endTime,
                   @Param("workDay") LocalDate workDay,
                   @Param("doctorId") UUID doctorId);

    @Query("""
            SELECT s FROM Schedule s
            WHERE s.doctor.lastName = :lastName
            AND s.doctor.firstName = :firstName
            AND s.doctor.middleName = :middleName
            """)
    List<Schedule> findByDoctorFullName(@Param("lastName") String lastName,
                                        @Param("firstName") String firstName,
                                        @Param("middleName") String middleName);
}