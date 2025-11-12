# Как загрузить тестовые данные в базу данных

## Способ 1: Через psql (рекомендуется)

1. Откройте командную строку/PowerShell
2. Подключитесь к базе данных:

```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter
```

3. Введите пароль: `sjsjsqo18ha5`

4. Выполните SQL скрипт:

```sql
\i 'D:/medBack/UserService/src/main/resources/test-data.sql'
```

Или можно выполнить напрямую:

```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

## Способ 2: Через pgAdmin

1. Откройте pgAdmin
2. Подключитесь к серверу PostgreSQL (localhost:5433)
3. Выберите базу данных `medicalcenter`
4. Откройте Query Tool (Ctrl+Shift+Q или Tools → Query Tool)
5. Откройте файл `D:\medBack\UserService\src\main\resources\test-data.sql`
6. Нажмите Execute (F5)

## Способ 3: Через DBeaver

1. Откройте DBeaver
2. Подключитесь к базе данных medicalcenter
3. SQL Editor → Open SQL Script
4. Выберите файл `test-data.sql`
5. Execute SQL Statement (Ctrl+Enter)

## Что содержится в тестовых данных

### Пациенты (5 записей)
- Michael Smith (ID: 11111111-1111-1111-1111-111111111111)
- Sarah Johnson (ID: 22222222-2222-2222-2222-222222222222)
- David Williams (ID: 33333333-3333-3333-3333-333333333333)
- Emily Brown (ID: 44444444-4444-4444-4444-444444444444)
- James Davis (ID: 55555555-5555-5555-5555-555555555555)

### Врачи (5 записей)
- Dr. Emily Carter - Терапевт (ID: aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa)
- Dr. Sarah Mitchell - Педиатр (ID: bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb)
- Dr. Michael Johnson - Кардиолог (ID: cccccccc-cccc-cccc-cccc-cccccccccccc)
- Dr. David Brown - Невролог (ID: dddddddd-dddd-dddd-dddd-dddddddddddd)
- Dr. Lisa Anderson - Дерматолог (ID: eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee)

### Услуги (7 записей)
- Консультация терапевта
- Консультация педиатра
- Кардиологическое обследование
- Неврологический осмотр
- Дерматологическая консультация
- Вакцинация
- Лабораторные анализы

### Визиты
- **8 прошлых визитов** (status: completed) с диагнозами и назначениями
- **7 будущих визитов** (status: scheduled)

### Дополнительно
- Расписание работы врачей
- Отзывы о врачах (5 записей)
- Тестовые сообщения в чате (5 записей)
- 1 оператор
- 1 менеджер

## Проверка загрузки данных

После загрузки проверьте данные:

```sql
-- Проверка всех таблиц
SELECT 
    'Patient' as table_name, COUNT(*) as count FROM Patient
UNION ALL SELECT 'Doctor', COUNT(*) FROM Doctor
UNION ALL SELECT 'Service', COUNT(*) FROM Service
UNION ALL SELECT 'Visit', COUNT(*) FROM Visit
UNION ALL SELECT 'schedule', COUNT(*) FROM schedule
UNION ALL SELECT 'doctorreviews', COUNT(*) FROM doctorreviews
UNION ALL SELECT 'chat_messages', COUNT(*) FROM chat_messages
UNION ALL SELECT 'manager', COUNT(*) FROM manager
UNION ALL SELECT 'operators', COUNT(*) FROM operators;

-- Ожидаемые результаты:
-- Patient: 5
-- Doctor: 5
-- Service: 7
-- Visit: 15 (8 past + 7 future)
-- schedule: 11
-- doctorreviews: 5
-- chat_messages: 5
-- manager: 1
-- operators: 1
```

### Проверка конкретных данных:

```sql
-- Проверка пациентов
SELECT patient_id, first_name, last_name, date_of_birth, gender FROM Patient;

-- Проверка врачей с рейтингом
SELECT doctor_id, first_name, last_name, specialty, rating FROM Doctor;

-- Проверка прошлых визитов
SELECT COUNT(*) FROM Visit WHERE status = 'completed';
-- Ожидается: 8

-- Проверка будущих визитов
SELECT COUNT(*) FROM Visit WHERE status = 'scheduled';
-- Ожидается: 7

-- Проверка услуг с врачами
SELECT s.name_of_service, s.cost, d.first_name || ' ' || d.last_name as doctor
FROM Service s
JOIN Doctor d ON s.doctor_id = d.doctor_id;

-- Проверка сообщений чата
SELECT senderName, content, type, timestamp 
FROM chat_messages 
ORDER BY timestamp DESC;
```

## Тестирование API с тестовыми данными

### Прошлые визиты пациента Michael Smith:
```bash
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past
```

### Будущие визиты пациента Michael Smith:
```bash
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/future
```

### Будущие приемы врача Emily Carter:
```bash
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/future
```

### История пациента Michael Smith у врача Emily Carter:
```bash
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/patient/11111111-1111-1111-1111-111111111111/past
```

### Получение сообщений чата:
```bash
curl http://localhost:8081/api/chat/messages
```

## Очистка данных (если нужно начать заново)

```sql
-- ВНИМАНИЕ: Это удалит все данные!
TRUNCATE TABLE chat_messages CASCADE;
TRUNCATE TABLE doctorreviews CASCADE;
TRUNCATE TABLE Visit CASCADE;
TRUNCATE TABLE schedule CASCADE;
TRUNCATE TABLE Service CASCADE;
TRUNCATE TABLE manager CASCADE;
TRUNCATE TABLE operators CASCADE;
TRUNCATE TABLE Doctor CASCADE;
TRUNCATE TABLE Patient CASCADE;
```

Затем снова загрузите test-data.sql.

## Важные замечания о структуре БД

⚠️ **Названия таблиц чувствительны к регистру:**
- `Patient`, `Doctor`, `Service`, `Visit` - PascalCase
- `manager`, `operators`, `schedule`, `doctorreviews` - lowercase
- `chat_messages` - snake_case

⚠️ **Ключевые отличия в столбцах:**
- Patient: используйте `date_of_birth` (НЕ `birth_date`)
- Service: используйте `name_of_service` (НЕ `name`), `cost` (НЕ `price`)
- Service: **обязательно** имеет `doctor_id` (каждая услуга принадлежит врачу)
- Schedule: `work_day` это конкретная дата (НЕ день недели!)
- ChatMessage: столбцы `senderId`, `senderName`, `attachmentUrl` (camelCase!)
- DoctorReview: `visit_id` обязателен, есть `is_approved`, `created_at`/`updated_at`

📖 **Полная документация структуры:** см. `DATABASE_STRUCTURE_NOTES.md`

