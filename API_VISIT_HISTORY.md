# API для истории посещений

## Новые endpoints для просмотра истории визитов

### Для пациента

### 1. Прошлые визиты пациента

```http
GET /api/visits/patient/{patientId}/past
```

**Параметры:**
- `patientId` - UUID пациента
- `page` - номер страницы (опционально, default: 0)
- `size` - размер страницы (опционально, default: 20)
- `sort` - сортировка (опционально)

**Пример запроса:**
```bash
curl http://localhost:8081/api/visits/patient/123e4567-e89b-12d3-a456-426614174000/past?page=0&size=10
```

**Ответ:**
```json
[
  {
    "id": "uuid",
    "dateOfVisit": "2025-10-15T10:30:00",
    "doctorId": "uuid",
    "doctorName": "Dr. Sarah Williams",
    "patientId": "uuid",
    "patientName": "Michael Smith",
    "status": "completed",
    "symptoms": "Головная боль",
    "diagnosis": "Мигрень",
    "prescription": "Препарат X, 2 раза в день"
  }
]
```

### 2. Будущие визиты пациента

```http
GET /api/visits/patient/{patientId}/future
```

**Параметры:**
- `patientId` - UUID пациента
- `page` - номер страницы (опционально, default: 0)
- `size` - размер страницы (опционально, default: 20)
- `sort` - сортировка (опционально)

**Пример запроса:**
```bash
curl http://localhost:8081/api/visits/patient/123e4567-e89b-12d3-a456-426614174000/future?page=0&size=10
```

**Ответ:**
```json
[
  {
    "id": "uuid",
    "dateOfVisit": "2025-11-20T14:00:00",
    "doctorId": "uuid",
    "doctorName": "Dr. Emily Carter",
    "patientId": "uuid",
    "patientName": "Michael Smith",
    "status": "scheduled",
    "symptoms": null,
    "diagnosis": null,
    "prescription": null
  }
]
```

### Для врача

### 3. Прошлые визиты врача

```http
GET /api/visits/doctor/{doctorId}/past
```

**Параметры:**
- `doctorId` - UUID врача
- `page` - номер страницы (опционально, default: 0)
- `size` - размер страницы (опционально, default: 20)
- `sort` - сортировка (опционально)

**Пример запроса:**
```bash
curl http://localhost:8081/api/visits/doctor/123e4567-e89b-12d3-a456-426614174001/past
```

### 4. Будущие визиты врача

```http
GET /api/visits/doctor/{doctorId}/future
```

**Параметры:**
- `doctorId` - UUID врача
- `page` - номер страницы (опционально, default: 0)
- `size` - размер страницы (опционально, default: 20)
- `sort` - сортировка (опционально)

**Пример запроса:**
```bash
curl http://localhost:8081/api/visits/doctor/123e4567-e89b-12d3-a456-426614174001/future
```

### 5. История визитов конкретного пациента к врачу

```http
GET /api/visits/doctor/{doctorId}/patient/{patientId}/past
```

**Описание:** Возвращает историю прошлых визитов конкретного пациента к конкретному врачу. Полезно для врача, чтобы видеть медицинскую историю пациента перед приемом.

**Параметры:**
- `doctorId` - UUID врача
- `patientId` - UUID пациента
- `page` - номер страницы (опционально, default: 0)
- `size` - размер страницы (опционально, default: 20)
- `sort` - сортировка (опционально)

**Пример запроса:**
```bash
curl http://localhost:8081/api/visits/doctor/123e4567-e89b-12d3-a456-426614174001/patient/123e4567-e89b-12d3-a456-426614174000/past
```

**Ответ:**
```json
[
  {
    "id": "uuid",
    "dateOfVisit": "2025-10-15T10:30:00",
    "doctorId": "uuid",
    "doctorName": "Dr. Emily Carter",
    "patientId": "uuid",
    "patientName": "Michael Smith",
    "status": "completed",
    "symptoms": "Кашель, температура",
    "diagnosis": "ОРВИ",
    "prescription": "Противовирусные препараты, постельный режим"
  },
  {
    "id": "uuid",
    "dateOfVisit": "2025-09-20T14:00:00",
    "doctorId": "uuid",
    "doctorName": "Dr. Emily Carter",
    "patientId": "uuid",
    "patientName": "Michael Smith",
    "status": "completed",
    "symptoms": "Профилактический осмотр",
    "diagnosis": "Здоров",
    "prescription": "Общие рекомендации"
  }
]
```

**Примечания:**
- Визиты отсортированы по дате в убывающем порядке (последние сначала)
- Возвращаются только завершенные визиты в прошлом
- Врач может видеть полную историю болезни пациента (симптомы, диагнозы, назначения)

## Существующие endpoints (для справки)

### Получить все визиты
```http
GET /api/visits
```

### Получить визит по ID
```http
GET /api/visits/{id}
```

### Получить визиты по ID врача
```http
GET /api/visits/doctor/{doctorId}
```

### Получить визиты по ID пациента
```http
GET /api/visits/patient/{patientId}
```

### Получить визиты по статусу
```http
GET /api/visits/status/{status}
```

Статусы: `scheduled`, `completed`, `cancelled`

### Создать визит
```http
POST /api/visits
Content-Type: application/json

{
  "dateOfVisit": "2025-12-01T10:00:00",
  "doctorId": "uuid",
  "patientId": "uuid",
  "status": "scheduled",
  "symptoms": "string",
  "diagnosis": "string",
  "prescription": "string"
}
```

### Обновить визит
```http
PUT /api/visits/{id}
Content-Type: application/json

{
  "dateOfVisit": "2025-12-01T10:00:00",
  "doctorId": "uuid",
  "patientId": "uuid",
  "status": "completed",
  "symptoms": "string",
  "diagnosis": "string",
  "prescription": "string"
}
```

### Удалить визит
```http
DELETE /api/visits/{id}
```

## Сводка по использованию API

### Сценарии использования для пациента:
1. **Просмотр своих прошлых визитов**: `GET /api/visits/patient/{patientId}/past`
2. **Просмотр своих будущих визитов**: `GET /api/visits/patient/{patientId}/future`

### Сценарии использования для врача:
1. **Просмотр своих будущих приемов**: `GET /api/visits/doctor/{doctorId}/future`
2. **Просмотр своих прошлых приемов**: `GET /api/visits/doctor/{doctorId}/past`
3. **Просмотр истории конкретного пациента**: `GET /api/visits/doctor/{doctorId}/patient/{patientId}/past`

### Пример: Врач готовится к приему

```bash
# Шаг 1: Врач смотрит свои будущие приемы на сегодня
curl http://localhost:8081/api/visits/doctor/{doctorId}/future?size=10

# Шаг 2: Видит, что следующий пациент - {patientId}
# Шаг 3: Загружает историю этого пациента
curl http://localhost:8081/api/visits/doctor/{doctorId}/patient/{patientId}/past

# Врач видит все прошлые визиты пациента с диагнозами и назначениями
```

## Общие примечания

- Прошлые визиты отсортированы по дате в убывающем порядке (новые сначала)
- Будущие визиты отсортированы по дате в возрастающем порядке (ближайшие сначала)
- Текущая дата/время определяется на стороне сервера
- Для пагинации используется стандартный Spring Data Pageable
- История пациента у врача показывает только визиты к этому конкретному врачу

