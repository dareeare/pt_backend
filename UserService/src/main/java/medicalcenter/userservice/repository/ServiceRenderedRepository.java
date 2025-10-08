package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.ServiceRendered;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRenderedRepository extends JpaRepository<ServiceRendered, UUID> {

    @Query("SELECT sr FROM ServiceRendered sr WHERE sr.visit.id = :visitId")
    List<ServiceRendered> findByVisitId(@Param("visitId") UUID visitId, Pageable pageable);

    @Query("SELECT sr FROM ServiceRendered sr WHERE sr.service.id = :serviceId")
    List<ServiceRendered> findByServiceId(@Param("serviceId") UUID serviceId, Pageable pageable);

    @Query("SELECT sr FROM ServiceRendered sr WHERE sr.visit.id = :visitId AND sr.service.id = :serviceId")
    Optional<ServiceRendered> findByVisitIdAndServiceId(@Param("visitId") UUID visitId,
                                                        @Param("serviceId") UUID serviceId);

    @Query("SELECT SUM(sr.actualCost) FROM ServiceRendered sr WHERE sr.visit.id = :visitId")
    BigDecimal calculateTotalCostByVisitId(@Param("visitId") UUID visitId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE ServiceRendered sr
            SET sr.visit.id = :visitId,
                sr.service.id = :serviceId,
                sr.actualCost = :actualCost
            WHERE sr.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                  @Param("visitId") UUID visitId,
                  @Param("serviceId") UUID serviceId,
                  @Param("actualCost") BigDecimal actualCost);
}