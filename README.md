# Backend (pt_backend)

## Краткое описание

Backend сервиса для веб‑приложения "Медицинский центр" — REST API для управления пользователями, врачами, услугами, расписанием и записями, авторизации по номеру телефона (SMS-код) / Email, рассылки уведомлений и формирования отчетов.

Проект реализует функции: онлайн‑запись, личные кабинеты, ролевая модель (Guest/Patient/Doctor/Operator/Manager), модерация отзывов.

## Технологии

* Java 21+ и Spring Boot
* Hibernate / JPA
* PostgreSQL
* Docker / Docker Compose
* Maven
* JUnit 5 (unit/integration tests)

## Архитектура

![All entities](images/er.png)

## API по Ролям Пользователей

### 1. Guest (Неавторизованный пользователь)
Доступны базовые операции регистрации и входа, а также просмотр общедоступной информации.

**Authentication:**
- `POST /api/auth/signup` - Регистрация пациента
- `POST /api/auth/login` - Вход в систему (получение токена)
- `POST /api/auth/refresh` - Обновление токена
- `POST /api/auth/verify` - Подтверждение email
- `POST /api/auth/resend` - Повторная отправка кода

**Public Data:**
- `GET /api/doctors` - Просмотр списка врачей
- `GET /api/doctors/{id}` - Просмотр профиля врача
- `GET /api/services` - Просмотр списка услуг
- `GET /api/reviews` - Просмотр отзывов

### 2. Patient (Пациент)
Доступ ко всем функциям Guest, плюс управление личным профилем и записями.

**Profile:**
- `GET /api/patients/{id}` - Просмотр своего профиля
- `PUT /api/patients/{id}` - Обновление данных профиля
- `POST /api/patients/{id}/avatar` - Загрузка фото

**Visits & Schedule:**
- `POST /api/visits` - Создание записи на прием
- `GET /api/visits/search/by-patient?patientId={id}` - История своих визитов
- `GET /api/schedules` - Просмотр расписания
- `GET /api/timeslots/available` - Поиск свободных слотов

**Feedback:**
- `POST /api/reviews` - Оставить отзыв о враче

### 3. Doctor (Врач)
Доступ к управлению своим профилем, просмотру пациентов и работе с расписанием.

**Workspace:**
- `GET /api/doctors/{id}` - Просмотр своего профиля
- `PUT /api/doctors/{id}` - Изменение данных
- `GET /api/schedules/search/by-doctor?doctorId={id}` - Личное расписание
- `GET /api/visits/search/by-doctor?doctorId={id}` - Список записей к врачу

**Patient Management:**
- `GET /api/patients` - Просмотр списка пациентов
- `GET /api/patients/{id}` - Просмотр карты пациента
- `POST /api/service-rendered` - Фиксация оказанных услуг

### 4. Manager / Operator (Администратор)
Полный доступ к управлению сущностями системы.

**User Management:**
- `POST /api/auth/signup/staff` - Регистрация сотрудников
- `GET /api/managers`, `GET /api/operators` - Списки персонала
- `DELETE /api/doctors/{id}`, `DELETE /api/patients/{id}` - Удаление пользователей

**Content Management:**
- `POST /api/services`, `PUT /api/services/{id}`, `DELETE` - Управление услугами
- `PUT /api/reviews/{id}/approve` - Модерация отзывов

**Schedule Management:**
- `POST /api/schedules` - Создание расписания
- `POST /api/schedule-exceptions` - Управление исключениями (больничные/отгулы)

## Documentation & Swagger

Интерактивная документация доступна после запуска:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **JSON Spec**: http://localhost:8080/api-docs

### Генерация клиента
`openapi-generator generate -i http://localhost:8080/api-docs -g java -o ./client`
