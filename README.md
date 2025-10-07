# Backend (pt_backend)

## Краткое описание

Backend сервиса для веб‑приложения "Медицинский центр" — REST API для управления пользователями, врачами, услугами, расписанием и записями, авторизации по номеру телефона (SMS-код), рассылки уведомлений и формирования отчетов для менеджеров.

Проект реализует функции, описанные в требованиях заказчика: онлайн‑запись, личные кабинеты пациентов и врачей, роли (Guest/Patient/Doctor/Operator/Manager/System), модерация отзывов, генерация отчетов и интеграции (SMS, e‑mail).

## Технологии

* Java 21+ и Spring Boot
* Hibernate / JPA
* PostgreSQL
* Docker / Docker Compose
* Maven
* JUnit 5 (unit/integration tests)

## Архитектура

![All entities](images/er.png)

## Роли пользователей и их действия

![patient abilities](images/patient.png)
![guest abilities](images/guest.png)
![manager abilities](images/manager.png)
![operator abilities](images/operator.png)


## Medical Center API Endpoints

### Patients
- `GET /patients`
- `GET /patients/{id}`
- `GET /patients/search/by-lastname?lastName={lastName}`
- `GET /patients/search/by-fullname?lastName={lastName}&firstName={firstName}&middleName={middleName}`
- `GET /patients/search/by-phone?phone={phone}`
- `POST /patients`
- `PUT /patients/{id}`
- `DELETE /patients/{id}`

### Doctors
- `GET /doctors`
- `GET /doctors/{id}`
- `GET /doctors/search/by-specialty?specialty={specialty}`
- `GET /doctors/search/by-rating?minRating={minRating}`
- `GET /doctors/{id}/rating`
- `GET /doctors/{id}/rating/stats`
- `POST /doctors`
- `PUT /doctors/{id}`
- `DELETE /doctors/{id}`

### Services
- `GET /services`
- `GET /services/{id}`
- `GET /services/search/by-doctor?doctorId={doctorId}`
- `GET /services/search/by-cost?minCost={minCost}&maxCost={maxCost}`
- `POST /services`
- `PUT /services/{id}`
- `DELETE /services/{id}`

### Visits
- `GET /visits`
- `GET /visits/{id}`
- `GET /visits/search/by-doctor?doctorId={doctorId}`
- `GET /visits/search/by-patient?patientId={patientId}`
- `GET /visits/search/by-status?status={status}`
- `POST /visits`
- `PUT /visits/{id}`
- `DELETE /visits/{id}`

### Schedule
- `GET /schedules`
- `GET /schedules/search/by-doctor?doctorId={doctorId}`
- `GET /schedules/search/by-date?workDay={yyyy-MM-dd}`
- `POST /schedules`
- `PUT /schedules/{id}`
- `DELETE /schedules/{id}`

### TimeSlots
- `GET /timeslots`
- `GET /timeslots/search/by-doctor?doctorId={doctorId}`
- `GET /timeslots/available`
- `GET /timeslots/available/by-doctor?doctorId={doctorId}`
- `GET /timeslots/search/by-date?slotDate={yyyy-MM-dd}`
- `PUT /timeslots/{id}/book?visitId={visitId}`
- `PUT /timeslots/{id}/release`

### DoctorReviews
- `GET /reviews`
- `GET /reviews/search/by-doctor?doctorId={doctorId}`
- `GET /reviews/search/by-patient?patientId={patientId}`
- `GET /reviews/search/by-visit?visitId={visitId}`
- `POST /reviews`
- `PUT /reviews/{id}/approve`
- `PUT /reviews/{id}`
- `DELETE /reviews/{id}`

### ServiceRendered
- `GET /service-rendered`
- `GET /service-rendered/search/by-visit?visitId={visitId}`
- `GET /service-rendered/visit/{visitId}/total-cost`
- `POST /service-rendered`

### ScheduleExceptions
- `GET /schedule-exceptions`
- `GET /schedule-exceptions/search/by-doctor?doctorId={doctorId}`
- `POST /schedule-exceptions`

### Operators
- `GET /operators`
- `POST /operators`

### Managers
- `GET /managers`
- `POST /managers`

### Common Parameters
- `page` - page number (starts from 0)
- `size` - page size (default: 20)
- `sort` - sorting field (e.g., `lastName,asc`)
