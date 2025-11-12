# Quick Reference Cheat Sheet

## 🚀 Quick Start (3 Commands)

```bash
# 1. Load test data
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"

# 2. Start backend (in new terminal)
cd D:\medBack\UserService && mvn spring-boot:run

# 3. Start frontend (in new terminal)
cd D:\medCenter && npm start
```

Password for PostgreSQL: `sjsjsqo18ha5`

## 📊 Test Data IDs

### Patients:
- **Michael Smith**: `11111111-1111-1111-1111-111111111111` (3 past visits, 2 future)
- **Sarah Johnson**: `22222222-2222-2222-2222-222222222222` (2 past, 1 future)
- **David Williams**: `33333333-3333-3333-3333-333333333333` (2 past, 2 future)
- **Emily Brown**: `44444444-4444-4444-4444-444444444444` (1 past, 1 future)
- **James Davis**: `55555555-5555-5555-5555-555555555555` (0 past, 1 future)

### Doctors:
- **Dr. Emily Carter** (General Practitioner): `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`
- **Dr. Sarah Mitchell** (Pediatrician): `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb`
- **Dr. Michael Johnson** (Cardiologist): `cccccccc-cccc-cccc-cccc-cccccccccccc`
- **Dr. David Brown** (Neurologist): `dddddddd-dddd-dddd-dddd-dddddddddddd`
- **Dr. Lisa Anderson** (Dermatologist): `eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee`

### Staff:
- **John Operator**: `00000000-0000-0000-0000-000000000010`
- **Alice Manager**: `f0000000-0000-0000-0000-000000000020`

## 🔗 Important URLs

- **Backend API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Frontend**: http://localhost:4200
- **WebSocket**: ws://localhost:8081/ws-support

## 📡 API Quick Tests

### Visit History:
```bash
# Patient's past visits
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past

# Patient's future visits
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/future

# Doctor's patient history
curl http://localhost:8081/api/visits/doctor/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/patient/11111111-1111-1111-1111-111111111111/past
```

### Chat:
```bash
# Send message (type: USER, OPERATOR, or SYSTEM)
curl -X POST http://localhost:8081/api/chat/send?messageType=OPERATOR \
  -H "Content-Type: application/json" \
  -d '{"content":"Test message"}'

# Get messages
curl http://localhost:8081/api/chat/messages
```

## 🗄️ Database Quick Queries

```sql
-- View all visits with patient and doctor names
SELECT 
    v.date_of_visit,
    p.first_name || ' ' || p.last_name as patient,
    d.first_name || ' ' || d.last_name as doctor,
    v.status,
    v.diagnosis
FROM visit v
JOIN patient p ON v.patient_id = p.patient_id
JOIN doctor d ON v.doctor_id = d.doctor_id
ORDER BY v.date_of_visit DESC;

-- View chat messages
SELECT sender_name, content, type, timestamp 
FROM chat_messages 
ORDER BY timestamp DESC;

-- View services with doctors
SELECT s.name_of_service, s.cost, d.first_name || ' ' || d.last_name as doctor
FROM service s
JOIN doctor d ON s.doctor_id = d.doctor_id;
```

## ⚠️ Important Notes

### MessageType Values:
- ✅ **USER** - patient messages
- ✅ **OPERATOR** - staff messages (call center, support)
- ✅ **SYSTEM** - automated system messages
- ❌ **ADMIN** - does NOT exist! Use OPERATOR instead

### Required Fields:
- `visit.diagnosis` - **NOT NULL** (use '' or 'Pending examination' for scheduled visits)
- `service.doctor_id` - **REQUIRED** (every service belongs to a doctor)
- `doctorreviews.visit_id` - **REQUIRED** (one review per visit)

### Table Name Case Sensitivity:
All tables are **lowercase**: patient, doctor, visit, service, etc.

### Column Names:
- Most use **snake_case**: `date_of_birth`, `first_name`, `sender_id`
- **NOT** camelCase

## 🛠️ Common Commands

### Clear All Data:
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "TRUNCATE TABLE chat_messages, doctorreviews, visit, schedule, service, manager, operators, doctor, patient CASCADE;"
```

### Reload Test Data:
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

### Check Data Counts:
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "SELECT 'patient', COUNT(*) FROM patient UNION ALL SELECT 'visit', COUNT(*) FROM visit;"
```

## 📚 Full Documentation

- 📘 `QUICK_START.md` - Complete setup guide
- 📘 `FINAL_CORRECTIONS.md` - All schema corrections
- 📘 `DATABASE_STRUCTURE_NOTES.md` - Schema reference
- 📘 `API_VISIT_HISTORY.md` - Visit API documentation
- 📘 `WEBSOCKET_POSTMAN_GUIDE.md` - WebSocket testing
- 📘 `LOAD_TEST_DATA.md` - Data loading guide

---

**Ready to go!** 🎉

