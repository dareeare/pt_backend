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

## Развертывание и запуск приложения

### Требования

Перед развертыванием убедитесь, что установлены следующие компоненты:

- **Docker** версии 20.10 или выше
- **Docker Compose** версии 1.29 или выше
- **Java 21+** (только для локальной разработки без Docker)
- **Maven 3.9+** (только для локальной разработки без Docker)
- **PostgreSQL 16+** (только при использовании локальных баз данных без Docker)

### Настройка переменных окружения

Создайте файл `.env` в корневой директории проекта `pt_backend` со следующим содержимым:

```env
# База данных
DB_USER=postgres
DB_PASSWORD=your_secure_password

# JWT настройки
JWT_SECRET_KEY=your_jwt_secret_key_min_256_bits
JWT_ACCESS_EXP_MS=900000
JWT_REFRESH_EXP_MS=86400000

# Настройки почтового сервера (для отправки email подтверждений)
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password

# OpenAPI настройки (опционально)
MEDICALCENTER_OPENAPI_DEV_URL=http://localhost:8081
MEDICALCENTER_OPENAPI_PROD_URL=https://medicalcenter-api.com
```

**Важно:**
- Замените все значения на ваши собственные
- `JWT_SECRET_KEY` должен быть длиной минимум 256 бит (32 символа)
- Для Gmail используйте пароль приложения, а не обычный пароль от аккаунта
- Файл `.env` не должен попадать в систему контроля версий (добавьте в `.gitignore`)

Также создайте файлы `.env` в директориях `AuthService` и `UserService`, если требуется переопределить настройки для конкретных сервисов.

### Развертывание с помощью Docker Compose (рекомендуется)

1. **Клонируйте репозиторий** (если еще не сделали):
   ```bash
   git clone <repository-url>
   cd pt_backend
   ```

2. **Создайте файл `.env`** в корне проекта с необходимыми переменными (см. раздел выше)

3. **Запустите все сервисы:**
   ```bash
   docker-compose up -d
   ```

   Эта команда создаст и запустит:
   - **AuthService** на порту 8080
   - **UserService** на порту 8081
   - **PostgreSQL база данных для AuthService** на порту 5435
   - **PostgreSQL база данных для UserService** на порту 5433

4. **Проверьте статус контейнеров:**
   ```bash
   docker-compose ps
   ```

5. **Просмотрите логи** (если нужно):
   ```bash
   # Все сервисы
   docker-compose logs -f
   
   # Конкретный сервис
   docker-compose logs -f authservice
   docker-compose logs -f userservice
   ```

6. **Остановка сервисов:**
   ```bash
   docker-compose down
   ```

7. **Остановка с удалением данных (volumes):**
   ```bash
   docker-compose down -v
   ```

### Локальный запуск без Docker (для разработки)

#### 1. Настройка баз данных PostgreSQL

Создайте две базы данных:
```sql
-- База данных для AuthService
CREATE DATABASE authservice;

-- База данных для UserService
CREATE DATABASE medicalcenter;
```

#### 2. Настройка AuthService

1. Перейдите в директорию `AuthService`:
   ```bash
   cd AuthService
   ```

2. Отредактируйте `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/authservice
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   ```

3. Соберите и запустите:
   ```bash
   mvn clean package
   java -jar target/AuthService-0.0.1-SNAPSHOT.jar
   ```

   Или запустите напрямую через Maven:
   ```bash
   mvn spring-boot:run
   ```

#### 3. Настройка UserService

1. Перейдите в директорию `UserService`:
   ```bash
   cd UserService
   ```

2. Отредактируйте `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/medicalcenter
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   ```

3. Соберите и запустите:
   ```bash
   mvn clean package
   java -jar target/UserService-0.0.1-SNAPSHOT.jar
   ```

   Или запустите напрямую через Maven:
   ```bash
   mvn spring-boot:run
   ```

### Проверка работоспособности

После запуска проверьте доступность сервисов:

1. **AuthService Health Check:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **UserService Health Check:**
   ```bash
   curl http://localhost:8081/actuator/health
   ```

3. **Swagger UI AuthService:**
   - Откройте в браузере: http://localhost:8080/swagger-ui.html

4. **Swagger UI UserService:**
   - Откройте в браузере: http://localhost:8081/swagger-ui.html

### Порты сервисов

- **8080** - AuthService (авторизация и аутентификация)
- **8081** - UserService (основной бизнес-логика)
- **5433** - PostgreSQL UserService (внешний порт при использовании Docker)
- **5435** - PostgreSQL AuthService (внешний порт при использовании Docker)

### Миграции базы данных

Миграции базы данных выполняются автоматически при запуске сервисов через Liquibase (AuthService). UserService использует `spring.jpa.hibernate.ddl-auto=update` для автоматического обновления схемы.

### Решение проблем

#### Проблема: Контейнеры не запускаются

1. Проверьте логи:
   ```bash
   docker-compose logs
   ```

2. Убедитесь, что порты не заняты:
   ```bash
   # Windows
   netstat -ano | findstr :8080
   netstat -ano | findstr :8081
   
   # Linux/Mac
   lsof -i :8080
   lsof -i :8081
   ```

#### Проблема: Ошибки подключения к базе данных

1. Проверьте, что базы данных запущены:
   ```bash
   docker-compose ps
   ```

2. Проверьте переменные окружения в `.env` файле

3. Проверьте логи баз данных:
   ```bash
   docker-compose logs userdb
   docker-compose logs authdb
   ```

#### Проблема: Миграции не применяются

1. Проверьте логи сервисов на наличие ошибок миграций
2. Убедитесь, что файлы миграций присутствуют в `src/main/resources/db/changelog/`

### Запуск тестов

Для запуска тестов:

```bash
# AuthService
cd AuthService
mvn test

# UserService
cd UserService
mvn test
```

## Документация и Swagger

Интерактивная документация доступна после запуска:
- **Swagger UI AuthService**: http://localhost:8080/swagger-ui.html
- **Swagger UI UserService**: http://localhost:8081/swagger-ui.html
- **JSON Spec AuthService**: http://localhost:8080/api-docs
- **JSON Spec UserService**: http://localhost:8081/api-docs

### Генерация клиента
```bash
openapi-generator generate -i http://localhost:8080/api-docs -g java -o ./client
```
