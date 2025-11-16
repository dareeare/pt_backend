package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.Operator;
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
public interface OperatorRepository extends JpaRepository<Operator, UUID> {

    @Query("SELECT o FROM Operator o WHERE o.lastName = :lastName")
    List<Operator> findAllByLastName(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT o FROM Operator o WHERE o.firstName = :firstName AND o.lastName = :lastName")
    List<Operator> findAllByLastFirstName(@Param("firstName") String firstName,
                                          @Param("lastName") String lastName,
                                          Pageable pageable);

    @Query("SELECT o FROM Operator o WHERE o.lastName = :lastName AND o.firstName = :firstName AND o.middleName = :middleName")
    Optional<Operator> findByFullName(@Param("lastName") String lastName,
                                      @Param("firstName") String firstName,
                                      @Param("middleName") String middleName);

    @Query("SELECT o FROM Operator o WHERE o.phone = :phone")
    Optional<Operator> findByPhone(@Param("phone") String phone);

    @Query("SELECT o FROM Operator o WHERE o.email = :email")
    Optional<Operator> findByEmail(@Param("email") String email);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Operator o
            SET o.lastName = :lastName,
                o.firstName = :firstName,
                o.middleName = :middleName,
                o.dateOfBirth = :dateOfBirth,
                o.phone = :phone,
                o.email = :email,
                o.avatarPath = :avatarPath
            WHERE o.id = :id
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
    @Query("UPDATE Operator o SET o.avatarPath = :avatarPath WHERE o.id = :id")
    @Transactional

    int updateAvatarPath(@Param("id") UUID id, @Param("avatarPath") String avatarPath);
}