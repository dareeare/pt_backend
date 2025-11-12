# 🚀 Быстрый старт медицинского центра

## Предварительные требования

- ✅ PostgreSQL 15+ (работает на порту 5433)
- ✅ Java 17+
- ✅ Node.js 18+
- ✅ Maven

## Шаг 1: Загрузка тестовых данных в БД

### Через командную строку (быстрее всего):
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```
Пароль: `sjsjsqo18ha5`

### Или через pgAdmin/DBeaver:
См. подробную инструкцию в `LOAD_TEST_DATA.md`

## Шаг 2: Запуск Backend (UserService)

```bash
cd D:\medBack\UserService
mvn clean install
mvn spring-boot:run
```

Сервер запустится на: http://localhost:8081

### Проверка запуска:
```bash
curl http://localhost:8081/api/chat/messages
```

## Шаг 3: Запуск Frontend

```bash
cd D:\medCenter
npm install  # если не выполняли ранее
npm start
```

Фронтенд будет доступен на: http://localhost:4200

## Тестовые данные

После загрузки test-data.sql у вас будет:

### Пациенты для тестирования:
- **Michael Smith** (ID: `11111111-1111-1111-1111-111111111111`)
  - Телефон: +1234567890
  - Email: michael.smith@email.com
  - Имеет 3 прошлых визита и 2 будущих

- **Sarah Johnson** (ID: `22222222-2222-2222-2222-222222222222`)
  - Телефон: +1234567891
  - Email: sarah.johnson@email.com
  - Имеет 2 прошлых визита и 1 будущий

### Врачи:
- **Dr. Emily Carter** (Терапевт) - ID: `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`
- **Dr. Sarah Mitchell** (Педиатр) - ID: `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb`
- **Dr. Michael Johnson** (Кардиолог) - ID: `cccccccc-cccc-cccc-cccc-cccccccccccc`
- **Dr. David Brown** (Невролог) - ID: `dddddddd-dddd-dddd-dddd-dddddddddddd`
- **Dr. Lisa Anderson** (Дерматолог) - ID: `eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee`

### Staff:
- **John Operator** - ID: `00000000-0000-0000-0000-000000000010`
- **Alice Manager** - ID: `f0000000-0000-0000-0000-000000000020`

## Тестирование API

### 1. История визитов пациента

#### Прошлые визиты:
```bash
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past
```

#### Будущие визиты:
```bash
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/future
```

### 2. Расписание врача

#### Будущие приемы врача:
```bash
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/future
```

#### История пациента у врача:
```bash
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/patient/11111111-1111-1111-1111-111111111111/past
```

### 3. Чат (групповая рассылка)

#### Отправить сообщение всем:
```bash
curl -X POST http://localhost:8081/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{"content":"Important announcement for all users!"}'
```

#### Получить историю сообщений:
```bash
curl http://localhost:8081/api/chat/messages?size=20
```

#### Посмотреть онлайн пользователей:
```bash
curl http://localhost:8081/api/chat/online-users
```

## Тестирование через фронтенд

### 1. Страница пользователя
- Перейдите: http://localhost:4200/user
- Увидите кнопку "Chat" с возможным счетчиком непрочитанных
- Кнопка "Service archive" покажет прошлые и будущие визиты

### 2. Страница оператора
- Перейдите: http://localhost:4200/operator
- Нажмите на кнопку "Chat"
- Откроется страница оператора чата для рассылки

### 3. Тестирование чата
1. Откройте 2 вкладки браузера
2. В одной зайдите как пользователь: `/chat-user`
3. В другой как оператор: `/chat-operator`
4. Отправьте сообщение от оператора
5. Увидите его в реальном времени у пользователя!

### 4. Тестирование уведомлений
1. Находясь на `/user`, отправьте сообщение через Postman или другую вкладку
2. Увидите красную точку с цифрой на кнопке Chat
3. При открытии чата точка исчезнет

## Проверка данных в БД

### Все пациенты:
```sql
SELECT first_name, last_name, phone, email FROM Patient;
```

### Все визиты с информацией:
```sql
SELECT 
    v.date_of_visit,
    p.first_name || ' ' || p.last_name as patient,
    d.first_name || ' ' || d.last_name as doctor,
    v.status,
    v.diagnosis
FROM Visit v
JOIN Patient p ON v.patient_id = p.patient_id
JOIN Doctor d ON v.doctor_id = d.doctor_id
ORDER BY v.date_of_visit DESC;
```

### Сообщения чата:
```sql
SELECT sender_name, content, timestamp, type 
FROM Chat_Message 
ORDER BY timestamp DESC 
LIMIT 10;
```

## Полезные ссылки

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/api-docs
- **Фронтенд**: http://localhost:4200

## Документация

- 📘 `API_VISIT_HISTORY.md` - Полная документация API для истории визитов
- 📗 `WEBSOCKET_TESTING.md` - Подробное руководство по WebSocket
- 📙 `WEBSOCKET_POSTMAN_GUIDE.md` - Быстрый гайд для Postman
- 📕 `LOAD_TEST_DATA.md` - Инструкции по загрузке тестовых данных

## Troubleshooting

### Backend не запускается
1. Проверьте что PostgreSQL работает: `psql -h localhost -p 5433 -U postgres -l`
2. Проверьте настройки в `application.properties`
3. Проверьте логи: `tail -f logs/userservice/userservice.log`

### Frontend не запускается
1. Удалите `node_modules` и выполните `npm install` заново
2. Проверьте версию Node.js: `node --version` (нужна 18+)
3. Очистите кэш: `npm cache clean --force`

### WebSocket не работает
1. Убедитесь что backend запущен
2. Откройте консоль браузера (F12) и проверьте ошибки
3. Попробуйте через REST API: `POST /api/chat/send`

### Нет данных в БД
1. Проверьте что скрипт выполнился: 
```sql
SELECT COUNT(*) FROM Patient;  -- должно быть 5
SELECT COUNT(*) FROM Visit;    -- должно быть 15
```
2. Если пусто - загрузите test-data.sql снова

## Быстрая очистка и перезагрузка данных

```bash
# Очистка
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "
TRUNCATE TABLE Chat_Message, Doctor_Review, Visit, Schedule, Service, Manager, Operator, Doctor, Patient CASCADE;
"

# Загрузка
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

## Готовые сценарии тестирования

### Сценарий 1: Пациент просматривает свою историю
```bash
# 1. Получить прошлые визиты
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past

# 2. Получить будущие визиты
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/future
```

### Сценарий 2: Врач готовится к приему
```bash
# 1. Посмотреть свои будущие приемы
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/future

# 2. Загрузить историю следующего пациента (Michael Smith)
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/patient/11111111-1111-1111-1111-111111111111/past
```

### Сценарий 3: Оператор делает рассылку
```bash
# 1. Отправить объявление
curl -X POST http://localhost:8081/api/chat/send?messageType=OPERATOR \
  -H "Content-Type: application/json" \
  -d '{"content":"Dear patients! The clinic will be closed tomorrow."}'

# 2. Проверить что сообщение дошло
curl http://localhost:8081/api/chat/messages?size=5
```

---

**Готово!** 🎉 Все работает и готово к тестированию!

