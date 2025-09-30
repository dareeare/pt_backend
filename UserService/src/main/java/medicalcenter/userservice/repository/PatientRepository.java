package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    @Query("SELECT p FROM Patient p WHERE p.email = :email")
    List<Patient> findAllByEmail(@Param("email") String email);

    @Query("SELECT p FROM Patient p WHERE p.lastName = :lastName")
    List<Patient> findAllByLastName(String lastName, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.firstName = :firstName AND p.lastName = :lastName")
    List<Patient> findAllByLastFirstName(@Param("lastName") String lastName, @Param("firstName") String firstName, Pageable pageable);

    @Query("""
            SELECT p FROM Patient p
            WHERE p.lastName = :lastName
                  AND p.firstName = :firstName
                  AND p.middleName = :middleName
            """)
    Optional<Patient> findByFullName(
            @Param("lastName") String lastName,
            @Param("firstName") String firstName,
            @Param("middleName") String middleName
    );

    @Query("SELECT p FROM Patient p WHERE p.phone = :phone")
    Optional<Patient> findByPhone(String phone);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            """
                    UPDATE Patient p
                    SET
                        p.lastName = :lastName,
                        p.firstName = :firstName,
                        p.middleName = :middleName,
                        p.phone = :phone,
                        p.email = :email,
                        p.dateOfBirth = :dateOfBirth,
                        p.gender = :gender
                    WHERE p.id = :id
                    """)
    @Transactional
    int updateById(
            @Param("id") UUID id,
            @Param("lastName") String lastName,
            @Param("firstName") String firstName,
            @Param("middleName") String middleName,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("dateOfBirth") LocalDate dateOfBirth,
            @Param("gender") char gender);
}
