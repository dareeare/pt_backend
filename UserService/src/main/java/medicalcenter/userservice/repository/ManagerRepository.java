package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Manager;
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
public interface ManagerRepository extends JpaRepository<Manager, UUID> {

    @Query("SELECT m FROM Manager m WHERE m.lastName = :lastName")
    List<Manager> findAllByLastName(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT m FROM Manager m WHERE m.firstName = :firstName AND m.lastName = :lastName")
    List<Manager> findAllByLastFirstName(@Param("firstName") String firstName,
                                         @Param("lastName") String lastName,
                                         Pageable pageable);

    @Query("SELECT m FROM Manager m WHERE m.lastName = :lastName AND m.firstName = :firstName AND m.middleName = :middleName")
    Optional<Manager> findByFullName(@Param("lastName") String lastName,
                                     @Param("firstName") String firstName,
                                     @Param("middleName") String middleName);

    @Query("SELECT m FROM Manager m WHERE m.phone = :phone")
    Optional<Manager> findByPhone(@Param("phone") String phone);

    @Query("SELECT m FROM Manager m WHERE m.email = :email")
    Optional<Manager> findByEmail(@Param("email") String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Manager m
            SET m.lastName = :lastName,
                m.firstName = :firstName,
                m.middleName = :middleName,
                m.dateOfBirth = :dateOfBirth,
                m.phone = :phone,
                m.email = :email,
                m.avatarPath = :avatarPath
            WHERE m.id = :id
            """)
    @Transactional
    int updateById(@Param("id") UUID id,
                   @Param("lastName") String lastName,
                   @Param("firstName") String firstName,
                   @Param("middleName") String middleName,
                   @Param("dateOfBirth") LocalDate dateOfBirth,
                   @Param("phone") String phone,
                   @Param("email") String email,
                   @Param("avatarPath") String avatarPath);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Manager m SET m.avatarPath = :avatarPath WHERE m.id = :id")
    @Transactional
    int updateAvatarPath(@Param("id") UUID id, @Param("avatarPath") String avatarPath);
}