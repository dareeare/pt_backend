# Final Corrections Based on Actual Database Schema

## ✅ All Corrections Applied

### 1. Port Changed: 8080 → 8081

**Files Updated:**
- ✅ `UserService/src/main/resources/application.properties`
- ✅ `medCenter/proxy.conf.json`
- ✅ `medCenter/src/app/services/chat.service.ts`
- ✅ All documentation files

**New URLs:**
- Backend API: http://localhost:8081
- WebSocket: ws://localhost:8081/ws-support
- Swagger UI: http://localhost:8081/swagger-ui.html

### 2. Test Data Fully in English

All Russian text translated to English:
- ✅ Doctor specialties
- ✅ Service names and descriptions
- ✅ Visit symptoms, diagnoses, prescriptions
- ✅ Patient reviews
- ✅ Chat messages

### 3. Database Schema Corrections

Based on actual schema from `my.txt`:

#### Table Names (case-sensitive):
- ✅ `patient` (lowercase)
- ✅ `doctor` (lowercase)
- ✅ `service` (lowercase)
- ✅ `visit` (lowercase)
- ✅ `manager` (lowercase)
- ✅ `operators` (lowercase)
- ✅ `schedule` (lowercase)
- ✅ `doctorreviews` (lowercase, no underscore)
- ✅ `chat_messages` (snake_case)

#### Column Name Corrections:

**patient table:**
- ✅ All columns in snake_case
- ✅ `date_of_birth` (NOT birth_date)
- ❌ REMOVED: `address` field does not exist

**doctor table:**
- ✅ All columns in snake_case
- ✅ `information` field for bio/experience
- ✅ `rating` field (numeric)
- ❌ REMOVED: `experience_years` does not exist

**service table:**
- ✅ `name_of_service` (NOT `name`)
- ✅ `cost` (NOT `price`)
- ✅ `information` (NOT `description`)
- ✅ `doctor_id` (required FK)

**visit table:**
- ✅ `diagnosis` is **NOT NULL** (required!)
  - For scheduled visits, use: `'Pending examination'` or empty string `''`

**manager table:**
- ✅ `date_of_birth` (NOT hire_date)
- ❌ REMOVED: `department`, `hire_date` do not exist

**operators table:**
- ✅ `date_of_birth` (NOT hire_date)
- ❌ REMOVED: `hire_date` does not exist

**schedule table:**
- ✅ `work_day` (date) - specific calendar date
- ✅ `start_time` (timestamp) - full datetime
- ✅ `end_time` (timestamp) - full datetime

**doctorreviews table:**
- ✅ `visit_id` (required, unique FK)
- ✅ `is_approved` (boolean)
- ✅ `is_edited` (boolean)
- ✅ `created_at` (timestamp)
- ✅ `updated_at` (timestamp)
- ❌ REMOVED: `review_date` does not exist

**chat_messages table:**
- ✅ `id` (NOT message_id)
- ✅ `sender_id` (snake_case)
- ✅ `sender_name` (snake_case)
- ✅ `content` (text, NOT NULL)
- ✅ `timestamp` (NOT NULL)
- ✅ `type` - CHECK constraint: **USER, OPERATOR, SYSTEM** only!
- ✅ `attachment_url` (snake_case)

### 4. Critical: MessageType Enum

**Allowed values:** `USER`, `OPERATOR`, `SYSTEM`

**NOT allowed:** ~~`ADMIN`~~

**Changes:**
- ✅ Frontend: Changed all `'ADMIN'` to `'OPERATOR'`
- ✅ Chat service: Updated interface type
- ✅ Chat admin component: Sends messages as `'OPERATOR'`
- ✅ Test data: Messages use `'OPERATOR'` type
- ✅ All documentation updated

## How to Load Corrected Test Data

### Step 1: Clear Old Data

```sql
TRUNCATE TABLE chat_messages, doctorreviews, visit, schedule, 
               service, manager, operators, doctor, patient CASCADE;
```

Or via psql:
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "TRUNCATE TABLE chat_messages, doctorreviews, visit, schedule, service, manager, operators, doctor, patient CASCADE;"
```

### Step 2: Load Corrected Data

```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

Password: `sjsjsqo18ha5`

### Step 3: Verify Data Loaded

```sql
SELECT 
    'patient' as table_name, COUNT(*) as count FROM patient
UNION ALL SELECT 'doctor', COUNT(*) FROM doctor
UNION ALL SELECT 'service', COUNT(*) FROM service
UNION ALL SELECT 'visit', COUNT(*) FROM visit
UNION ALL SELECT 'schedule', COUNT(*) FROM schedule
UNION ALL SELECT 'doctorreviews', COUNT(*) FROM doctorreviews
UNION ALL SELECT 'chat_messages', COUNT(*) FROM chat_messages
UNION ALL SELECT 'manager', COUNT(*) FROM manager
UNION ALL SELECT 'operators', COUNT(*) FROM operators;
```

**Expected Results:**
```
table_name     | count
---------------+-------
patient        |     5
doctor         |     5
service        |     7
visit          |    15
schedule       |    11
doctorreviews  |     5
chat_messages  |     5
manager        |     1
operators      |     1
```

## Test the Corrected Data

### 1. Test Visit History API:

```bash
# Past visits for patient Michael Smith (should return 3 visits)
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past

# Future visits for patient Michael Smith (should return 2 visits)
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/future

# Doctor's patient history (should return 2 visits)
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/patient/11111111-1111-1111-1111-111111111111/past
```

### 2. Test Chat with OPERATOR Type:

```bash
# Send message as OPERATOR (NOT ADMIN!)
curl -X POST http://localhost:8081/api/chat/send?messageType=OPERATOR \
  -H "Content-Type: application/json" \
  -d '{"content":"Test broadcast message"}'

# Verify it saved with correct type
curl http://localhost:8081/api/chat/messages?size=1
```

Expected response type: **"OPERATOR"** (not "ADMIN")

### 3. Verify English Data:

```bash
# Check doctor specialties are in English
curl http://localhost:8081/api/doctors

# Check visit diagnoses are in English
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past
```

Expected data examples:
- Specialty: "General Practitioner" (not "Терапевт")
- Diagnosis: "Acute respiratory infection" (not "ОРВИ")
- Symptoms: "Cough, fever 37.5C" (not "Кашель, температура")

## Restart Services

After loading corrected data:

### Restart Backend (new port 8081):
```bash
cd D:\medBack\UserService
# Stop if running (Ctrl+C)
mvn spring-boot:run
```

### Restart Frontend (proxy updated):
```bash
cd D:\medCenter
# Stop if running (Ctrl+C)
npm start
```

## What Was Fixed

### ❌ Previous Issues:

1. Wrong port (8080 instead of 8081)
2. Mixed Russian/English data
3. Wrong column names:
   - `birth_date` → should be `date_of_birth`
   - `name` → should be `name_of_service`
   - `price` → should be `cost`
   - `description` → should be `information`
   - `message_id` → should be `id`
   - camelCase in chat_messages → should be snake_case
4. Wrong table names:
   - `Doctor_Review` → should be `doctorreviews`
   - `Chat_Message` → should be `chat_messages`
   - `Operator` → should be `operators`
5. Missing required fields:
   - `doctor_id` in service (required FK!)
   - `visit_id` in doctorreviews (required FK!)
   - `diagnosis` in visit (NOT NULL!)
6. Wrong enum value: 'ADMIN' → should be 'OPERATOR'
7. Non-existent fields:
   - `address` in patient
   - `experience_years` in doctor
   - `hire_date` in manager/operator
   - `department` in manager

### ✅ All Fixed!

Now the SQL script matches the **exact database schema** from your PostgreSQL database.

## Quick Test Commands

```bash
# Load data
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"

# Verify counts
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "SELECT 'patient', COUNT(*) FROM patient UNION ALL SELECT 'doctor', COUNT(*) FROM doctor UNION ALL SELECT 'visit', COUNT(*) FROM visit;"

# Test API
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past

# Test chat
curl -X POST http://localhost:8081/api/chat/send -H "Content-Type: application/json" -d '{"content":"Test"}'

# View chat messages
curl http://localhost:8081/api/chat/messages
```

---

**Status:** ✅ Database schema fully validated and corrected!
**Date:** November 9, 2025
**Based on:** Actual PostgreSQL schema from my.txt

