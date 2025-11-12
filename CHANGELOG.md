# Changelog - November 9, 2025

## ⚠️ CRITICAL CORRECTIONS (Based on Actual DB Schema)

### UUID Format Fix
- ✅ Fixed invalid UUID formats (removed non-hex characters: v, g, s, etc.)
- ✅ All UUIDs now use only valid hex digits (0-9, a-f)
- ✅ Test data loads without UUID syntax errors

### Component Renaming: Manager/Admin → Operator
- ✅ `manager-start` → `operator-start` (operator home screen)
- ✅ `chat-admin` → `chat-operator` (operator chat interface)
- ✅ Routes updated: `/operator` and `/chat-operator`
- ✅ All UI labels changed from "Admin" to "Operator"
- ✅ Matches database structure and MessageType enum

### Database Schema Validation
- ✅ Validated against actual PostgreSQL schema from `my.txt`
- ✅ Corrected all table and column names
- ✅ Fixed data types and constraints
- ✅ All test data now matches real DB structure

### Key Fixes:

**Table Names:**
- All lowercase: `patient`, `doctor`, `visit`, `service`, `manager`, `operators`, `schedule`, `doctorreviews`, `chat_messages`

**Critical Column Corrections:**
- ✅ `chat_messages`: All columns use snake_case (`sender_id`, `sender_name`, `attachment_url`)
- ✅ `service`: `name_of_service` (NOT `name`), `cost` (NOT `price`), `information` (NOT `description`)
- ✅ `visit`: `diagnosis` is **NOT NULL** (required!)
- ✅ `patient`: `date_of_birth` (NOT `birth_date`)
- ✅ MessageType enum: **USER, OPERATOR, SYSTEM** (NOT ADMIN!)

**Removed Non-Existent Fields:**
- ❌ `address` from patient
- ❌ `experience_years` from doctor
- ❌ `hire_date` and `department` from manager/operator

### Documentation Created:
- 📘 `FINAL_CORRECTIONS.md` - Complete list of all fixes
- 📘 `DATABASE_STRUCTURE_NOTES.md` - Detailed schema reference

## Changes Made

### 🔧 Port Change: 8080 → 8081

**Backend (UserService):**
- ✅ `application.properties` - changed `server.port=8081`
- ✅ `application.properties` - updated OpenAPI dev URL to `http://localhost:8081`

**Frontend:**
- ✅ `proxy.conf.json` - updated both proxy targets to `http://localhost:8081`
- ✅ `chat.service.ts` - updated WebSocket URL to `http://localhost:8081/ws-support`

**Documentation:**
- ✅ `QUICK_START.md` - all URLs updated to port 8081
- ✅ `API_VISIT_HISTORY.md` - all URLs updated to port 8081
- ✅ `WEBSOCKET_TESTING.md` - all URLs updated to port 8081
- ✅ `WEBSOCKET_POSTMAN_GUIDE.md` - all URLs updated to port 8081
- ✅ `LOAD_TEST_DATA.md` - all URLs updated to port 8081

### 🌐 Language Update: Test Data Fully in English

**Before:** Mixed Russian and English
**After:** All data in English

**Changed in `test-data.sql`:**

#### Doctor Specialties (translated):
- Терапевт → General Practitioner
- Педиатр → Pediatrician
- Кардиолог → Cardiologist
- Невролог → Neurologist
- Дерматолог → Dermatologist

#### Services (translated):
- Консультация терапевта → General Practitioner Consultation
- Консультация педиатра → Pediatric Consultation
- Кардиологическое обследование → Cardiology Examination
- Неврологический осмотр → Neurological Examination
- Дерматологическая консультация → Dermatology Consultation
- Вакцинация → Vaccination
- Лабораторные анализы → Laboratory Tests

#### Visit Data (translated):
- All symptoms (симптомы) → English
- All diagnoses (диагноз) → English
- All prescriptions (назначения) → English
- All reviews (отзывы) → English
- All chat messages → English

**Examples:**
- "Кашель, температура 37.5" → "Cough, fever 37.5C"
- "ОРВИ" → "Acute respiratory infection"
- "Противовирусные препараты" → "Antiviral medication"
- "Головная боль, усталость" → "Headache, fatigue"
- "Переутомление" → "Overwork syndrome"

## How to Apply Changes

### 1. Reload Test Data

Clear old data and load new English data:

```bash
# Clear database
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "
TRUNCATE TABLE Chat_Message, Doctor_Review, Visit, Schedule, Service, Manager, Operator, Doctor, Patient CASCADE;
"

# Load new English data
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

### 2. Restart Backend

```bash
cd D:\medBack\UserService
mvn spring-boot:run
```

Backend will now run on: **http://localhost:8081**

### 3. Restart Frontend

If frontend was running, restart it to apply proxy changes:

```bash
cd D:\medCenter
# Press Ctrl+C to stop if running
npm start
```

Frontend remains on: **http://localhost:4200**

## Testing the Changes

### Verify Port Change:

```bash
# Should return data on new port
curl http://localhost:8081/api/chat/messages

# Old port should not work
curl http://localhost:8080/api/chat/messages
# Expected: Connection refused or timeout
```

### Verify English Data:

```bash
# Check doctors in English
curl http://localhost:8081/api/doctors

# Check visits with English symptoms/diagnosis
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past
```

Expected response should have English text:
```json
{
  "symptoms": "Cough, fever 37.5C",
  "diagnosis": "Acute respiratory infection",
  "prescription": "Antiviral medication, bed rest for 5 days"
}
```

### Verify WebSocket:

```bash
# Test chat with new port
curl -X POST http://localhost:8081/api/chat/send \
  -H "Content-Type: application/json" \
  -d '{"content":"Test message on new port"}'
```

## Updated URLs

All services now use port **8081**:

- ✅ Swagger UI: http://localhost:8081/swagger-ui.html
- ✅ API Docs: http://localhost:8081/api-docs
- ✅ REST API: http://localhost:8081/api/*
- ✅ WebSocket: ws://localhost:8081/ws-support
- ✅ Frontend proxy: automatically routes to 8081

## Benefits

### Port Change:
- ✅ Avoids conflicts with other services on port 8080
- ✅ Better separation of services (AuthService can use 8080)
- ✅ Clearer service identification

### English Data:
- ✅ Better for international development
- ✅ Easier to demo and share
- ✅ More professional for documentation
- ✅ Compatible with API clients worldwide
- ✅ No encoding issues

## Rollback (if needed)

If you need to revert to port 8080:

### Backend:
```properties
# In application.properties
server.port=8080
medicalcenter.openapi.dev-url=http://localhost:8080
```

### Frontend:
```json
// In proxy.conf.json
"target": "http://localhost:8080"

// In chat.service.ts
new SockJS('http://localhost:8080/ws-support')
```

Then restart both services.

## Notes

- ⚠️ Make sure no other service is using port 8081
- ⚠️ Update any external API clients to use new port
- ⚠️ Browser may cache old WebSocket connection - do hard refresh (Ctrl+Shift+R)
- ✅ All test data IDs remain the same for consistency
- ✅ Database structure unchanged - only data content translated

---

**Status:** ✅ All changes applied successfully
**Date:** November 9, 2025

