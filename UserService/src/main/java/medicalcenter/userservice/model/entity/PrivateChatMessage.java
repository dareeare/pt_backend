package medicalcenter.userservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import medicalcenter.userservice.model.PrivateMessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "private_chat_messages", indexes = {
    @Index(columnList = "chatRoomId, timestamp"),
    @Index(columnList = "senderName, recipient"),
    @Index(columnList = "timestamp")
})

@Entity
public class PrivateChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String senderName;

    private String senderAvatarUrl;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String chatRoomId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrivateMessageType type;

    private String attachmentUrl;

    private boolean read;
}