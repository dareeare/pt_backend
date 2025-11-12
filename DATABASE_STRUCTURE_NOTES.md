# Database Structure - Important Notes

## Key Differences Between Expected and Actual Schema

### Table Names (Case Sensitivity)

| Entity | Table Name in DB |
|--------|------------------|
| Patient | `Patient` (PascalCase) |
| Doctor | `Doctor` (PascalCase) |
| Service | `Service` (PascalCase) |
| Visit | `Visit` (PascalCase) |
| Manager | `manager` (lowercase) ⚠️ |
| Operator | `operators` (lowercase, plural) ⚠️ |
| Schedule | `schedule` (lowercase) ⚠️ |
| DoctorReview | `doctorreviews` (lowercase, no underscore) ⚠️ |
| ChatMessage | `chat_messages` (snake_case) ⚠️ |

### Patient Table

Actual columns:
- `patient_id` (UUID, primary key)
- `first_name` (varchar)
- `last_name` (varchar)
- `middle_name` (varchar, nullable)
- `date_of_birth` ⚠️ (NOT `birth_date`)
- `phone` (varchar)
- `email` (varchar)
- `gender` (varchar - M/F/O)

**REMOVED:** `address` field does not exist!

### Doctor Table

Actual columns:
- `doctor_id` (UUID, primary key)
- `first_name` (varchar)
- `last_name` (varchar)
- `middle_name` (varchar, nullable)
- `specialty` (varchar)
- `phone` (varchar)
- `email` (varchar)
- `information` ⚠️ (text field for bio/experience)
- `rating` (decimal, calculated from reviews)

**REMOVED:** `experience_years` field does not exist!

### Service Table

Actual columns:
- `service_id` (UUID, primary key)
- `name_of_service` ⚠️ (NOT `name`)
- `cost` ⚠️ (NOT `price`)
- `duration_minutes` (integer)
- `information` ⚠️ (NOT `description`)
- `doctor_id` ⚠️ (foreign key - service belongs to ONE doctor!)

**IMPORTANT:** Each service is linked to a specific doctor via `doctor_id`.

### Manager Table

Actual columns:
- `manager_id` (UUID, primary key)
- `first_name` (varchar)
- `last_name` (varchar)
- `middle_name` (varchar, nullable)
- `date_of_birth` ⚠️ (NOT `hire_date`)
- `phone` (varchar)
- `email` (varchar)

**REMOVED:** `department` and `hire_date` fields do not exist!

### Operator Table

Actual columns:
- `operator_id` (UUID, primary key)
- `first_name` (varchar)
- `last_name` (varchar)
- `middle_name` (varchar, nullable)
- `date_of_birth` ⚠️ (NOT `hire_date`)
- `phone` (varchar)
- `email` (varchar)

**REMOVED:** `hire_date` field does not exist!

### Visit Table

Actual columns:
- `visit_id` (UUID, primary key)
- `date_of_visit` (timestamp)
- `doctor_id` (UUID, foreign key)
- `patient_id` (UUID, foreign key)
- `status` (varchar - scheduled/completed/cancelled)
- `symptoms` (varchar(255), nullable)
- `diagnosis` (varchar(255), **NOT NULL**) ⚠️ REQUIRED!
- `prescription` (varchar(255), nullable)

**CRITICAL:** `diagnosis` is NOT NULL! For scheduled visits, use empty string '' or 'Pending examination'.

### Schedule Table

Actual columns:
- `schedule_id` (UUID, primary key)
- `doctor_id` (UUID, foreign key)
- `work_day` ⚠️ (date - specific calendar date, NOT day of week!)
- `start_time` ⚠️ (timestamp - full datetime, NOT just time!)
- `end_time` ⚠️ (timestamp - full datetime, NOT just time!)

**MAJOR CHANGE:** 
- Old concept: Recurring weekly schedule (e.g., "every Monday 9:00-17:00")
- New concept: Specific date schedule (e.g., "November 15, 2025, 9:00-17:00")

### DoctorReview Table

Actual columns:
- `review_id` (UUID, primary key)
- `doctor_id` (UUID, foreign key)
- `patient_id` (UUID, foreign key)
- `visit_id` ⚠️ (UUID, foreign key - OneToOne with Visit!)
- `rating` (integer, 1-5)
- `comment` (text, nullable)
- `is_approved` ⚠️ (boolean, default false)
- `is_edited` ⚠️ (boolean, default false)
- `created_at` ⚠️ (timestamp)
- `updated_at` ⚠️ (timestamp)

**REMOVED:** `review_date` - use `created_at` instead!

**NEW FIELDS:** Moderation system with `is_approved`, `is_edited`, `created_at`, `updated_at`.

### ChatMessage Table (chat_messages)

Actual columns:
- `id` (UUID, primary key) ⚠️ (NOT `message_id`)
- `sender_id` (UUID, nullable) ✅ snake_case
- `sender_name` (varchar) ✅ snake_case
- `content` (text)
- `timestamp` (timestamp)
- `type` (varchar - CHECK constraint: USER/OPERATOR/SYSTEM) ⚠️ NOT ADMIN!
- `attachment_url` (varchar, nullable) ✅ snake_case

**IMPORTANT:** Type can only be: **USER**, **OPERATOR**, or **SYSTEM** (NOT ADMIN!)

## Foreign Key Relationships

### Service → Doctor (Many-to-One)
Each service MUST have a `doctor_id`. Services cannot exist without a doctor.

### DoctorReview → Visit (One-to-One)
Each review MUST reference a specific `visit_id`. One review per visit.

### DoctorReview → Doctor & Patient (Many-to-One)
Reviews link to both doctor and patient.

### Visit → Doctor & Patient (Many-to-One)
Standard relationship.

### Schedule → Doctor (Many-to-One)
Each schedule entry belongs to one doctor.

## Migration Guide

### If you have old test data with wrong column names:

1. **Clear the database:**
```sql
TRUNCATE TABLE chat_messages, doctorreviews, Visit, schedule, Service, manager, operators, Doctor, Patient CASCADE;
```

2. **Load new corrected data:**
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

## Additional Tables in Database

The database also contains these tables (currently not populated with test data):

### time_slots
- `slot_id` (UUID, primary key)
- `slot_date` (date)
- `start_time` (time)
- `end_time` (time)
- `doctor_id` (UUID, foreign key)
- `visit_id` (UUID, foreign key, unique, nullable)

### service_rendered
- `sr_id` (UUID, primary key)
- `service_id` (UUID, foreign key)
- `visit_id` (UUID, foreign key)
- `actual_cost` (numeric)

### scheduleexceptions
- `exception_id` (UUID, primary key)
- `doctor_id` (UUID, foreign key)
- `exception_date` (date)
- `is_working_day` (boolean)
- `reason` (varchar)
- `created_at` (timestamp)

## Common Errors and Solutions

### Error: "new row for relation 'chat_messages' violates check constraint"
**Solution:** Type must be 'USER', 'OPERATOR', or 'SYSTEM'. Change 'ADMIN' to 'OPERATOR'.

### Error: "null value in column 'diagnosis' violates not-null constraint"
**Solution:** diagnosis is required! For scheduled visits, use '' or 'Pending examination'.

### Error: "column 'birth_date' does not exist"
**Solution:** Use `date_of_birth` instead.

### Error: "column 'name' does not exist in Service"
**Solution:** Use `name_of_service` instead.

### Error: "column 'price' does not exist"
**Solution:** Use `cost` instead.

### Error: "null value in column 'doctor_id' violates not-null constraint"
**Solution:** All services MUST have a doctor_id. Link each service to a specific doctor.

### Error: "insert or update on table 'doctorreviews' violates foreign key constraint"
**Solution:** Make sure the visit_id exists in Visit table before creating a review.

### Error: "column 'day_of_week' does not exist"
**Solution:** Use `work_day` with full date (e.g., '2025-11-15'), not day name.

### Error: "column 'review_date' does not exist"
**Solution:** Use `created_at` instead.

## Validation Query

After loading data, verify with:

```sql
-- Check all tables have data
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
```

Expected result:
- Patient: 5
- Doctor: 5
- Service: 7
- Visit: 15 (8 past + 7 future)
- schedule: 11
- doctorreviews: 5
- chat_messages: 5
- manager: 1
- operators: 1

## Notes on JPA Naming Strategy

The project appears to use a mixed naming strategy:
- Most tables use explicit `@Table(name = "...")` annotations
- Some use PascalCase (Patient, Doctor, Service, Visit)
- Others use lowercase (manager, operators, schedule, doctorreviews)
- ChatMessage uses snake_case (chat_messages)

Column names:
- Most use snake_case (first_name, date_of_birth)
- ChatMessage fields without @Column use camelCase (senderId, senderName)

**Recommendation:** Always check the Entity class for exact column names before writing SQL!

