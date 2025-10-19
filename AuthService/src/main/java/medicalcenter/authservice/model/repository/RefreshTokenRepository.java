package medicalcenter.authservice.model.repository;

import medicalcenter.authservice.model.entity.RefreshToken;
import medicalcenter.authservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByUserId(Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expiryDate < :now")
    void deleteAllExpiredSince(Instant now);

    void deleteByUser(User user);

    void deleteByUserId(Long userId);
}
