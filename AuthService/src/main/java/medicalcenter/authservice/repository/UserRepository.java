package medicalcenter.authservice.repository;

import medicalcenter.authservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);
}
