package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findAllByOrderByTimestampDesc(Pageable pageable);
}
