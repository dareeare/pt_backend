# Инструкция по тестированию WebSocket через Postman

## Обзор

В UserService реализована система групповой рассылки сообщений через WebSocket с использованием STOMP протокола и SockJS.

### Технические детали:
- **WebSocket Endpoint**: `ws://localhost:8081/ws-support`
- **Протокол**: STOMP over WebSocket/SockJS
- **Topic для рассылки**: `/topic/support`
- **Application prefix**: `/app`

## Настройка Postman

### Шаг 1: Установка необходимых инструментов

Postman поддерживает WebSocket начиная с версии 9.15. Убедитесь, что у вас установлена актуальная версия Postman.

### Шаг 2: Создание WebSocket запроса

1. Откройте Postman
2. Создайте новый запрос типа **WebSocket Request**
3. Введите URL подключения:
   ```
   ws://localhost:8081/ws-support
   ```

### Шаг 3: Подключение к WebSocket

1. Нажмите кнопку **Connect**
2. После успешного подключения вы увидите сообщение о статусе соединения

## Тестирование STOMP протокола

### Отправка STOMP CONNECT фрейма

После установления WebSocket соединения необходимо отправить STOMP CONNECT фрейм:

```
CONNECT
accept-version:1.2
heart-beat:10000,10000

^@
```

**Важно**: `^@` это NULL byte (символ \0), который обозначает конец фрейма в STOMP.

### Подписка на topic

После успешного CONNECT отправьте SUBSCRIBE фрейм:

```
SUBSCRIBE
id:sub-0
destination:/topic/support

^@
```

### Отправка сообщения

Отправьте SEND фрейм для публикации сообщения:

```
SEND
destination:/app/support
content-type:application/json

{"senderName":"TestUser","content":"Hello from Postman!","type":"USER"}
^@
```

## Альтернативный способ: Использование REST API

Для более простого тестирования можно использовать REST API endpoints:

### 1. Отправка сообщения через REST

```http
POST http://localhost:8081/api/chat/send
Content-Type: application/json

{
  "content": "Тестовое сообщение для всех пользователей",
  "attachmentUrl": null
}
```

**Query параметры** (опционально):
- `senderId` - UUID отправителя
- `messageType` - тип сообщения: USER, ADMIN, SYSTEM

### 2. Получение последних сообщений

```http
GET http://localhost:8081/api/chat/messages?page=0&size=50
```

### 3. Поиск сообщений

```http
GET http://localhost:8081/api/chat/messages/search?query=test&page=0&size=50
```

### 4. Получение списка онлайн пользователей

```http
GET http://localhost:8081/api/chat/online-users
```

## Использование специализированных инструментов

Для более удобного тестирования WebSocket рекомендуется использовать:

### 1. Simple WebSocket Client (Chrome Extension)

1. Установите расширение из Chrome Web Store
2. Подключитесь к: `ws://localhost:8081/ws-support`
3. Отправьте STOMP фреймы как описано выше

### 2. websocat (командная строка)

```bash
# Установка (если нужно)
# Windows: choco install websocat
# Linux: cargo install websocat

# Подключение
websocat ws://localhost:8081/ws-support

# После подключения отправляйте STOMP фреймы
```

### 3. wscat (Node.js)

```bash
# Установка
npm install -g wscat

# Подключение
wscat -c ws://localhost:8081/ws-support

# После подключения отправляйте STOMP фреймы
```

## Примеры тестовых сценариев

### Сценарий 1: Отправка сообщения всем пользователям

1. Подключитесь через WebSocket
2. Отправьте CONNECT фрейм
3. Подпишитесь на `/topic/support`
4. Отправьте сообщение через `/app/support`
5. Сообщение должно быть получено всеми подписанными клиентами

### Сценарий 2: Отправка через REST API

```bash
curl -X POST http://localhost:8081/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Важное объявление для всех пользователей!"
  }' \
  -G --data-urlencode "messageType=OPERATOR"
```

### Сценарий 3: Проверка typing indicator

```
SEND
destination:/app/typing
content-type:application/json

{"username":"TestUser","isTyping":true}
^@
```

## Структура сообщений

### ChatMessageDto (отправка через WebSocket)

```json
{
  "id": "uuid-string",
  "senderId": "uuid-string",
  "senderName": "Имя отправителя",
  "content": "Текст сообщения",
  "timestamp": "2025-11-09T14:30:00",
  "type": "USER|OPERATOR|SYSTEM",
  "attachmentUrl": "url-to-attachment"
}
```

### SendMessageRequest (отправка через REST)

```json
{
  "content": "Текст сообщения",
  "attachmentUrl": "url-to-attachment"
}
```

### TypingEvent

```json
{
  "username": "Имя пользователя",
  "isTyping": true
}
```

### OnlineUsersResponse

```json
{
  "users": ["user1", "user2", "user3"],
  "count": 3
}
```

## Проверка работоспособности

### 1. Проверка WebSocket endpoint

```bash
curl -i -N -H "Connection: Upgrade" \
     -H "Upgrade: websocket" \
     -H "Host: localhost:8081" \
     http://localhost:8081/ws-support
```

Должен вернуться ответ с кодом 101 (Switching Protocols).

### 2. Проверка REST API

```bash
# Получение последних сообщений
curl http://localhost:8081/api/chat/messages

# Получение онлайн пользователей
curl http://localhost:8081/api/chat/online-users
```

## Troubleshooting

### Проблема: Не удается подключиться к WebSocket

**Решение:**
1. Убедитесь, что UserService запущен на порту 8080
2. Проверьте, что в конфигурации разрешены CORS запросы
3. Проверьте логи приложения

### Проблема: Сообщения не приходят

**Решение:**
1. Убедитесь, что вы подписались на `/topic/support`
2. Проверьте формат STOMP фреймов (не забывайте NULL byte)
3. Проверьте логи сервера

### Проблема: 401 Unauthorized

**Решение:**
1. Если требуется аутентификация, добавьте заголовок Authorization
2. Проверьте конфигурацию Spring Security

## Мониторинг

### Логи сервера

В логах UserService можно отслеживать:
- Подключения пользователей
- Отправленные сообщения
- Онлайн пользователей

Пример логов:
```
INFO  User TestUser is now online. Total online: 1
DEBUG Received message from TestUser: Hello from Postman!
INFO  User TestUser is now offline. Total online: 0
```

## Полезные команды для разработки

### Просмотр логов в реальном времени

```bash
# Windows PowerShell
Get-Content D:\medBack\UserService\logs\userservice.log -Wait -Tail 50

# Linux/Mac
tail -f D:\medBack\UserService\logs\userservice.log
```

### Очистка логов

```bash
# Windows PowerShell
Clear-Content D:\medBack\UserService\logs\userservice.log

# Linux/Mac
echo "" > D:\medBack\UserService\logs\userservice.log
```

## Заключение

Для тестирования групповой рассылки WebSocket в Postman:

1. **Простой способ**: Используйте REST API endpoint `/api/chat/send`
2. **Продвинутый способ**: Используйте WebSocket запросы с STOMP протоколом
3. **Рекомендуемый способ**: Используйте специализированные инструменты (wscat, websocat) для более удобного тестирования

REST API endpoints предоставляют полную функциональность для тестирования без необходимости работы с низкоуровневым WebSocket протоколом.

