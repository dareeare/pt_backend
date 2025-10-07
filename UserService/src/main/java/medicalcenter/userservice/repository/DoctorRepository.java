package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Doctor;
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
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    @Query("SELECT d FROM Doctor d WHERE d.lastName = :lastName")
    List<Doctor> findAllByLastName(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.firstName = :firstName AND d.lastName = :lastName")
    List<Doctor> findAllByLastFirstName(@Param("firstName") String firstName,
                                        @Param("lastName") String lastName,
                                        Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.lastName = :lastName AND d.firstName = :firstName AND d.middleName = :middleName")
    Optional<Doctor> findByFullName(@Param("lastName") String lastName,
                                    @Param("firstName") String firstName,
                                    @Param("middleName") String middleName);

    @Query("SELECT d FROM Doctor d WHERE d.phone = :phone")
    Optional<Doctor> findByPhone(@Param("phone") String phone);

    @Query("SELECT d FROM Doctor d WHERE d.specialty = :specialty")
    List<Doctor> findBySpecialty(@Param("specialty") String specialty, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.rating >= :minRating")
    List<Doctor> findByRatingGreaterThanEqual(@Param("minRating") Double minRating, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Doctor d
            SET d.lastName = :lastName,
                d.firstName = :firstName,
                d.middleName = :middleName,
                d.specialty = :specialty,
                d.phone = :phone,
                d.email = :email,
                d.information = :information,
                d.rating = :rating
            WHERE d.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                   @Param("lastName") String lastName,
                   @Param("firstName") String firstName,
                   @Param("middleName") String middleName,
                   @Param("specialty") String specialty,
                   @Param("phone") String phone,
                   @Param("email") String email,
                   @Param("information") String information,
                   @Param("rating") BigDecimal rating);
}
