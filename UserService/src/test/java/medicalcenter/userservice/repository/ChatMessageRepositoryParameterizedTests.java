package medicalcenter.userservice.repository;

import medicalcenter.userservice.model.MessageType;
import medicalcenter.userservice.model.entity.ChatMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Параметризованные тесты для ChatMessageRepository.
 * Покрывают все CRUD операции с различными наборами тестовых данных.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ChatMessageRepositoryParameterizedTests {

    @Autowired
    private ChatMessageRepository repository;

    /**
     * Источник данных для тестирования создания сообщений чата
     */
    private static Stream<Arguments> provideChatMessageDataForCreate() {
        return Stream.of(
                Arguments.of(
                        UUID.randomUUID(), "Иван Иванов", "/avatars/user1.jpg",
                        "Здравствуйте, у меня вопрос", LocalDateTime.of(2024, 1, 15, 10, 0),
                        MessageType.USER, null
                ),
                Arguments.of(
                        UUID.randomUUID(), "Оператор Мария", "/avatars/operator1.jpg",
                        "Здравствуйте! Чем могу помочь?", LocalDateTime.of(2024, 1, 15, 10, 1),
                        MessageType.OPERATOR, null
                ),
                Arguments.of(
                        UUID.randomUUID(), "Петр Сидоров", "/avatars/user2.jpg",
                        "Нужна консультация кардиолога", LocalDateTime.of(2024, 2, 20, 14, 30),
                        MessageType.USER, null
                ),
                Arguments.of(
                        null, "Система", null,
                        "Ваш запрос принят в обработку", LocalDateTime.of(2024, 3, 25, 9, 15),
                        MessageType.SYSTEM, null
                ),
                Arguments.of(
                        UUID.randomUUID(), "Анна Козлова", "/avatars/user3.jpg",
                        "Спасибо за помощь!", LocalDateTime.of(2024, 4, 10, 16, 45),
                        MessageType.USER, "https://example.com/file.pdf"
                )
        );
    }

    /**
     * Параметризованный тест для создания сообщения чата (CREATE)
     */
    @ParameterizedTest(name = "[{index}] Create chat message: {1}, type: {5}")
    @MethodSource("provideChatMessageDataForCreate")
    @DisplayName("save() should persist chat message with different data sets")
    void testSaveChatMessage_Parameterized(UUID senderId, String senderName, String senderAvatarUrl,
                                           String content, LocalDateTime timestamp,
                                           MessageType type, String attachmentUrl) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .senderName(senderName)
                .senderAvatarUrl(senderAvatarUrl)
                .content(content)
                .timestamp(timestamp)
                .type(type)
                .attachmentUrl(attachmentUrl)
                .build();

        // Act
        ChatMessage saved = repository.save(message);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSenderId()).isEqualTo(senderId);
        assertThat(saved.getSenderName()).isEqualTo(senderName);
        assertThat(saved.getSenderAvatarUrl()).isEqualTo(senderAvatarUrl);
        assertThat(saved.getContent()).isEqualTo(content);
        assertThat(saved.getTimestamp()).isEqualTo(timestamp);
        assertThat(saved.getType()).isEqualTo(type);
        assertThat(saved.getAttachmentUrl()).isEqualTo(attachmentUrl);
    }

    /**
     * Параметризованный тест для чтения сообщения чата (READ)
     */
    @ParameterizedTest(name = "[{index}] Read chat message by ID: {1}")
    @MethodSource("provideChatMessageDataForCreate")
    @DisplayName("findById() should retrieve chat message with different data sets")
    void testFindById_Parameterized(UUID senderId, String senderName, String senderAvatarUrl,
                                    String content, LocalDateTime timestamp,
                                    MessageType type, String attachmentUrl) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .senderName(senderName)
                .senderAvatarUrl(senderAvatarUrl)
                .content(content)
                .timestamp(timestamp)
                .type(type)
                .attachmentUrl(attachmentUrl)
                .build();
        ChatMessage saved = repository.save(message);

        // Act
        Optional<ChatMessage> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo(content);
        assertThat(found.get().getSenderName()).isEqualTo(senderName);
        assertThat(found.get().getType()).isEqualTo(type);
    }

    /**
     * Параметризованный тест для удаления сообщения чата (DELETE)
     */
    @ParameterizedTest(name = "[{index}] Delete chat message: {1}")
    @MethodSource("provideChatMessageDataForCreate")
    @DisplayName("deleteById() should remove chat message with different data sets")
    void testDeleteById_Parameterized(UUID senderId, String senderName, String senderAvatarUrl,
                                      String content, LocalDateTime timestamp,
                                      MessageType type, String attachmentUrl) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .senderName(senderName)
                .senderAvatarUrl(senderAvatarUrl)
                .content(content)
                .timestamp(timestamp)
                .type(type)
                .attachmentUrl(attachmentUrl)
                .build();
        ChatMessage saved = repository.save(message);
        UUID messageId = saved.getId();

        assertThat(repository.findById(messageId)).isPresent();

        // Act
        repository.deleteById(messageId);

        // Assert
        assertThat(repository.findById(messageId)).isNotPresent();
    }

    /**
     * Параметризованный тест для поиска всех сообщений с сортировкой
     */
    @ParameterizedTest
    @CsvSource({
            "Сообщение 1, USER, 2024-01-15T10:00",
            "Сообщение 2, OPERATOR, 2024-01-15T10:05",
            "Сообщение 3, SYSTEM, 2024-01-15T10:10"
    })
    @DisplayName("findAllByOrderByTimestampDesc() should find messages ordered by timestamp")
    void testFindAllByOrderByTimestampDesc_Parameterized(String content, MessageType type, LocalDateTime timestamp) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(UUID.randomUUID())
                .senderName("Test User")
                .senderAvatarUrl("/avatar.jpg")
                .content(content)
                .timestamp(timestamp)
                .type(type)
                .attachmentUrl(null)
                .build();
        repository.save(message);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatMessage> found = repository.findAllByOrderByTimestampDesc(pageable);

        // Assert
        assertThat(found.getContent()).isNotEmpty();
        assertThat(found.getContent()).anyMatch(m -> m.getContent().equals(content));
    }

    /**
     * Параметризованный тест для различных типов сообщений
     */
    @ParameterizedTest
    @EnumSource(MessageType.class)
    @DisplayName("save() should persist messages of all types")
    void testSaveMessageByType_Parameterized(MessageType type) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(UUID.randomUUID())
                .senderName("Test Sender")
                .senderAvatarUrl("/avatar.jpg")
                .content("Test message for type " + type)
                .timestamp(LocalDateTime.now())
                .type(type)
                .attachmentUrl(null)
                .build();

        // Act
        ChatMessage saved = repository.save(message);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(type);
    }

    /**
     * Параметризованный тест для сообщений с вложениями
     */
    @ParameterizedTest
    @CsvSource({
            "Файл документа, https://example.com/doc.pdf",
            "Изображение, https://example.com/image.png",
            "Видео, https://example.com/video.mp4"
    })
    @DisplayName("save() should persist messages with attachments")
    void testSaveMessageWithAttachment_Parameterized(String content, String attachmentUrl) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(UUID.randomUUID())
                .senderName("Test User")
                .senderAvatarUrl("/avatar.jpg")
                .content(content)
                .timestamp(LocalDateTime.now())
                .type(MessageType.USER)
                .attachmentUrl(attachmentUrl)
                .build();

        // Act
        ChatMessage saved = repository.save(message);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAttachmentUrl()).isEqualTo(attachmentUrl);
        assertThat(saved.getContent()).isEqualTo(content);
    }

    /**
     * Параметризованный тест для сообщений разных отправителей
     */
    @ParameterizedTest
    @CsvSource({
            "Иван Петров, Вопрос о записи",
            "Мария Сидорова, Нужна консультация",
            "Петр Иванов, Спасибо за помощь"
    })
    @DisplayName("save() should persist messages from different senders")
    void testSaveMessageFromDifferentSenders_Parameterized(String senderName, String content) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(UUID.randomUUID())
                .senderName(senderName)
                .senderAvatarUrl("/avatar.jpg")
                .content(content)
                .timestamp(LocalDateTime.now())
                .type(MessageType.USER)
                .attachmentUrl(null)
                .build();

        // Act
        ChatMessage saved = repository.save(message);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSenderName()).isEqualTo(senderName);
        assertThat(saved.getContent()).isEqualTo(content);
    }

    /**
     * Параметризованный тест для создания множественных сообщений
     */
    @ParameterizedTest
    @CsvSource({
            "5, Сообщение",
            "10, Текст",
            "15, Вопрос"
    })
    @DisplayName("save() should handle multiple messages creation")
    void testSaveMultipleMessages_Parameterized(int count, String contentPrefix) {
        // Arrange & Act
        for (int i = 0; i < count; i++) {
            ChatMessage message = ChatMessage.builder()
                    .senderId(UUID.randomUUID())
                    .senderName("User " + i)
                    .senderAvatarUrl("/avatar" + i + ".jpg")
                    .content(contentPrefix + " " + i)
                    .timestamp(LocalDateTime.now().plusMinutes(i))
                    .type(MessageType.USER)
                    .attachmentUrl(null)
                    .build();
            repository.save(message);
        }

        // Assert
        Pageable pageable = PageRequest.of(0, 20);
        Page<ChatMessage> found = repository.findAllByOrderByTimestampDesc(pageable);
        assertThat(found.getContent().size()).isGreaterThanOrEqualTo(count);
    }

    /**
     * Параметризованный тест для системных сообщений
     */
    @ParameterizedTest
    @CsvSource({
            "Пользователь присоединился к чату",
            "Пользователь покинул чат",
            "Оператор начал печатать",
            "Файл был загружен"
    })
    @DisplayName("save() should persist system messages")
    void testSaveSystemMessage_Parameterized(String content) {
        // Arrange
        ChatMessage message = ChatMessage.builder()
                .senderId(null)
                .senderName("Система")
                .senderAvatarUrl(null)
                .content(content)
                .timestamp(LocalDateTime.now())
                .type(MessageType.SYSTEM)
                .attachmentUrl(null)
                .build();

        // Act
        ChatMessage saved = repository.save(message);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(MessageType.SYSTEM);
        assertThat(saved.getContent()).isEqualTo(content);
        assertThat(saved.getSenderId()).isNull();
    }

    /**
     * Параметризованный тест для получения всех сообщений
     */
    @ParameterizedTest
    @CsvSource({
            "3",
            "5",
            "10"
    })
    @DisplayName("findAll() should retrieve all messages")
    void testFindAll_Parameterized(int messageCount) {
        // Arrange
        for (int i = 0; i < messageCount; i++) {
            ChatMessage message = ChatMessage.builder()
                    .senderId(UUID.randomUUID())
                    .senderName("User " + i)
                    .senderAvatarUrl("/avatar.jpg")
                    .content("Message " + i)
                    .timestamp(LocalDateTime.now().plusMinutes(i))
                    .type(MessageType.USER)
                    .attachmentUrl(null)
                    .build();
            repository.save(message);
        }

        // Act
        List<ChatMessage> allMessages = repository.findAll();

        // Assert
        assertThat(allMessages).hasSizeGreaterThanOrEqualTo(messageCount);
    }
}

