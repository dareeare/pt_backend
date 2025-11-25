package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.entity.PrivateChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PrivateChatMessageRepository extends JpaRepository<PrivateChatMessage, UUID>, 
                                                     JpaSpecificationExecutor<PrivateChatMessage> {
    
    Page<PrivateChatMessage> findByChatRoomIdOrderByTimestampDesc(String chatRoomId, Pageable pageable);
    
    List<PrivateChatMessage> findByChatRoomIdOrderByTimestampAsc(String chatRoomId);
    
    @Query("SELECT m FROM PrivateChatMessage m WHERE " +
           "(m.senderName = :user1 AND m.recipient = :user2) OR " +
           "(m.senderName = :user2 AND m.recipient = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<PrivateChatMessage> findChatBetweenUsers(@Param("user1") String user1, 
                                                  @Param("user2") String user2);
    
    @Query("SELECT COUNT(m) FROM PrivateChatMessage m WHERE m.recipient = :username AND m.read = false")
    long countUnreadMessages(@Param("username") String username);
    
    @Query("SELECT COUNT(m) FROM PrivateChatMessage m WHERE m.recipient = :username AND m.senderName = :sender AND m.read = false")
    long countUnreadMessagesFromSender(@Param("username") String username, @Param("sender") String sender);
    
    @Modifying
    @Query("UPDATE PrivateChatMessage m SET m.read = true WHERE m.recipient = :username AND m.read = false")
    void markAllAsRead(@Param("username") String username);
    
    @Modifying
    @Query("UPDATE PrivateChatMessage m SET m.read = true WHERE m.recipient = :username AND m.senderName = :sender AND m.read = false")
    void markMessagesAsRead(@Param("username") String username, @Param("sender") String sender);
    
    @Query("SELECT DISTINCT m.senderName FROM PrivateChatMessage m WHERE m.recipient = :username " +
           "UNION " +
           "SELECT DISTINCT m.recipient FROM PrivateChatMessage m WHERE m.senderName = :username")
    List<String> findChatPartners(@Param("username") String username);
}