# Правильное тестирование WebSocket через Postman

## Проблема с прямым WebSocket в Postman

Postman поддерживает WebSocket, НО есть сложности с STOMP протоколом:
1. Нужно вручную формировать STOMP фреймы
2. NULL byte (`^@`) не всегда корректно обрабатывается
3. Сложно отлаживать

## ✅ РЕКОМЕНДУЕМЫЙ СПОСОБ: Использование REST API

Вместо прямого WebSocket используйте REST endpoint, который внутри отправляет сообщение через WebSocket:

### Отправка сообщения (будет сохранено в БД и разослано всем)

```http
POST http://localhost:8081/api/chat/send
Content-Type: application/json

{
  "content": "Important announcement for all users!"
}
```

**С параметрами:**
```http
POST http://localhost:8081/api/chat/send?messageType=OPERATOR&senderId=oooooooo-oooo-oooo-oooo-oooooooooooo
Content-Type: application/json

{
  "content": "Test broadcast from operator"
}
```

### Проверка сохранения в БД

После отправки проверьте в БД:

```sql
SELECT * FROM Chat_Message ORDER BY timestamp DESC LIMIT 10;
```

Или через REST API:

```bash
curl http://localhost:8081/api/chat/messages?size=10
```

## Альтернатива: wscat (намного проще чем Postman)

### Установка
```bash
npm install -g wscat
```

### Подключение
```bash
wscat -c ws://localhost:8081/ws-support
```

### После подключения отправьте:

1. **CONNECT** (подключение к STOMP):
```
CONNECT
accept-version:1.2
heart-beat:10000,10000

```
*Нажмите Enter дважды и отправьте NULL byte*

2. **SUBSCRIBE** (подписка на сообщения):
```
SUBSCRIBE
id:sub-0
destination:/topic/support

```

3. **SEND** (отправка сообщения):
```
SEND
destination:/app/support
content-type:application/json

{"senderName":"TestUser","content":"Hello from wscat!","type":"USER"}

```

## Проверка, что сообщение сохранилось

### Через psql:
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "SELECT * FROM Chat_Message ORDER BY timestamp DESC LIMIT 5;"
```

### Через REST API:
```bash
curl http://localhost:8081/api/chat/messages | json_pp
```

### Через pgAdmin/DBeaver:
```sql
SELECT 
    message_id,
    sender_name,
    content,
    timestamp,
    type
FROM Chat_Message 
ORDER BY timestamp DESC 
LIMIT 10;
```

## Пример полного теста через Postman REST API

### 1. Отправить сообщение как пользователь:
```
POST http://localhost:8081/api/chat/send
Content-Type: application/json

{
  "content": "Привет, это тестовое сообщение!"
}
```

**Ожидаемый ответ (код 201):**
```json
{
  "id": "uuid",
  "senderId": null,
  "senderName": "Anonymous",
  "content": "Привет, это тестовое сообщение!",
  "timestamp": "2025-11-09T15:30:00",
  "type": "USER",
  "attachmentUrl": null
}
```

### 2. Отправить сообщение как оператор:
```
POST http://localhost:8081/api/chat/send?messageType=OPERATOR
Content-Type: application/json

{
  "content": "Attention! Technical maintenance from 22:00 to 23:00"
}
```

### 3. Проверить что сообщения сохранились:
```
GET http://localhost:8081/api/chat/messages?page=0&size=20
```

**Ожидаемый ответ:**
```json
{
  "content": [
    {
      "id": "uuid-2",
      "senderName": "Anonymous",
      "content": "Attention! Technical maintenance...",
      "type": "OPERATOR",
      "timestamp": "2025-11-09T15:31:00"
    },
    {
      "id": "uuid-1",
      "senderName": "Anonymous",
      "content": "Привет, это тестовое сообщение!",
      "type": "USER",
      "timestamp": "2025-11-09T15:30:00"
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0
}
```

## Почему сообщение может не сохраниться

### 1. Неправильный формат STOMP фрейма
- Отсутствует NULL byte в конце
- Неправильные заголовки
- Неправильный JSON в теле

### 2. Проблемы с подключением
- WebSocket не установлен
- Нет CONNECT фрейма
- Нет подписки на topic

### 3. Ошибки на сервере
Проверьте логи:
```bash
tail -f D:\medBack\UserService\logs\userservice.log
```

Ищите строки с ошибками или:
```
INFO  User TestUser is now online. Total online: 1
DEBUG Received message from TestUser: Hello from Postman!
DEBUG Message saved: id=..., sender=TestUser, content=Hello...
```

## Рекомендации для тестирования

### Для разработки:
1. **Используйте REST API** (`/api/chat/send`) - проще всего
2. **Используйте wscat** - для тестирования WebSocket
3. **Используйте фронтенд** - для полного теста

### Для Postman:
- **REST API endpoints** вместо прямого WebSocket
- Это быстрее, проще и надежнее

### Команда для быстрой проверки:
```bash
# Отправить сообщение
curl -X POST http://localhost:8081/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{"content":"Test message"}'

# Проверить в БД
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "SELECT sender_name, content, timestamp FROM Chat_Message ORDER BY timestamp DESC LIMIT 3;"
```

## Тестирование рассылки (broadcast)

Когда сообщение отправляется через REST API или WebSocket:
1. ✅ Сохраняется в БД (таблица Chat_Message)
2. ✅ Рассылается всем подключенным клиентам через `/topic/support`
3. ✅ Возвращается в ответе

Чтобы увидеть рассылку в реальном времени:
1. Откройте несколько вкладок фронтенда
2. Зайдите в чат на каждой
3. Отправьте сообщение с одной вкладки
4. Увидите его на всех вкладках одновременно!

