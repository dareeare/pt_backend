package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    @Query("SELECT s FROM Service s WHERE LOWER(s.nameOfService) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Service> findByNameOfServiceContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT s FROM Service s WHERE s.doctor.id = :doctorId")
    List<Service> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT s FROM Service s WHERE s.cost BETWEEN :minCost AND :maxCost")
    List<Service> findByCostBetween(@Param("minCost") Double minCost, 
                                   @Param("maxCost") Double maxCost, 
                                   Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Service s
            SET s.nameOfService = :nameOfService,
                s.cost = :cost,
                s.durationMinutes = :durationMinutes,
                s.information = :information,
                s.doctor.id = :doctorId
            WHERE s.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                  @Param("nameOfService") String nameOfService,
                  @Param("cost") BigDecimal cost,
                  @Param("durationMinutes") Integer durationMinutes,
                  @Param("information") String information,
                  @Param("doctorId") UUID doctorId);

    @Query("SELECT s FROM Service s WHERE LOWER(s.information) LIKE LOWER(CONCAT('%', :info, '%'))")
    List<Service> findByInformation(@Param("info") String information, Pageable pageable);
}