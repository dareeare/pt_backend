package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.TimeSlot;
import medicalcenter.userservice.model.entity.Visit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctor.id = :doctorId")
    List<TimeSlot> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.slotDate = :slotDate")
    List<TimeSlot> findBySlotDate(@Param("slotDate") LocalDate slotDate, Pageable pageable);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctor.id = :doctorId AND ts.slotDate = :slotDate")
    List<TimeSlot> findByDoctorIdAndSlotDate(@Param("doctorId") UUID doctorId,
                                             @Param("slotDate") LocalDate slotDate,
                                             Pageable pageable);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.visit IS NULL")
    List<TimeSlot> findAvailableSlots(Pageable pageable);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctor.id = :doctorId AND ts.visit IS NULL")
    List<TimeSlot> findAvailableSlotsByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctor.id = :doctorId AND ts.slotDate = :slotDate AND ts.visit IS NULL")
    List<TimeSlot> findAvailableSlotsByDoctorIdAndDate(@Param("doctorId") UUID doctorId,
                                                       @Param("slotDate") LocalDate slotDate,
                                                       Pageable pageable);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.doctor.id = :doctorId AND ts.slotDate = :slotDate AND ts.startTime = :startTime")
    Optional<TimeSlot> findByDoctorIdAndSlotDateAndStartTime(@Param("doctorId") UUID doctorId,
                                                             @Param("slotDate") LocalDate slotDate,
                                                             @Param("startTime") LocalTime startTime);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.visit.id = :visitId")
    Optional<TimeSlot> findByVisitId(@Param("visitId") UUID visitId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE TimeSlot ts
            SET ts.doctor.id = :doctorId,
                ts.slotDate = :slotDate,
                ts.startTime = :startTime,
                ts.endTime = :endTime,
                ts.visit = :visit
            WHERE ts.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                   @Param("doctorId") UUID doctorId,
                   @Param("slotDate") LocalDate slotDate,
                   @Param("startTime") LocalTime startTime,
                   @Param("endTime") LocalTime endTime,
                   @Param("visit") Visit visit);
}