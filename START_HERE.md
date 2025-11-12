# 🚀 START HERE - Final Setup Instructions

## ✅ All Issues Fixed!

### What Was Fixed:

1. ✅ **UUID Format Error** - All UUIDs now use valid hex characters (0-9, a-f)
2. ✅ **Port Changed** - Backend moved from 8080 to **8081**
3. ✅ **Database Schema** - Corrected all table/column names to match real DB
4. ✅ **English Data** - All test data translated to English
5. ✅ **Component Naming** - Manager/Admin renamed to **Operator** (matches DB)
6. ✅ **MessageType** - Using OPERATOR instead of ADMIN
7. ✅ **Profile Bug** - Name and avatar persist across all pages

## 🎯 Quick Start (3 Steps)

### Step 1: Load Test Data

```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

Password: `sjsjsqo18ha5`

**Expected output:** All INSERT commands succeed, COMMIT at the end.

### Step 2: Start Backend

```bash
cd D:\medBack\UserService
mvn spring-boot:run
```

**Expected:** Server starts on http://localhost:8081

### Step 3: Start Frontend

```bash
cd D:\medCenter
npm start
```

**Expected:** App runs on http://localhost:4200

## ✅ Verify Everything Works

### 1. Check Database:
```bash
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "SELECT 'patient', COUNT(*) FROM patient UNION ALL SELECT 'visit', COUNT(*) FROM visit UNION ALL SELECT 'chat_messages', COUNT(*) FROM chat_messages;"
```

**Expected:**
```
 ?column? | count 
----------+-------
 patient  |     5
 visit    |    15
 chat_messages|    5
```

### 2. Test Visit API:
```bash
curl http://localhost:8081/api/visits/patient/11111111-1111-1111-1111-111111111111/past
```

**Expected:** Returns 3 past visits in English with diagnoses.

### 3. Test Chat API:
```bash
curl -X POST http://localhost:8081/api/chat/send?messageType=OPERATOR \
  -H "Content-Type: application/json" \
  -d '{"content":"Test message"}'
```

**Expected:** Returns created message with type "OPERATOR".

### 4. Test Frontend:

Open browser: http://localhost:4200

**User flow:**
1. Go to `/operator` - see operator home screen
2. Click "Chat" → opens `/chat-operator`
3. Send a message → should broadcast to all users

**Test with 2 browser tabs:**
1. Tab 1: http://localhost:4200/chat-operator (operator)
2. Tab 2: http://localhost:4200/chat-user (user)
3. Send message from Tab 1
4. See it appear in Tab 2 in real-time! ✨

## 📋 Test Data IDs

**Quick copy-paste for testing:**

```bash
# Patient (Michael Smith)
PATIENT_ID="11111111-1111-1111-1111-111111111111"

# Doctor (Dr. Emily Carter)
DOCTOR_ID="aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

# Operator (John Operator)
OPERATOR_ID="00000000-0000-0000-0000-000000000010"

# Test commands:
curl http://localhost:8081/api/visits/patient/$PATIENT_ID/past
curl http://localhost:8081/api/visits/doctor/$DOCTOR_ID/future
curl http://localhost:8081/api/visits/doctor/$DOCTOR_ID/patient/$PATIENT_ID/past
```

## ❌ If Something Goes Wrong

### Error: UUID syntax error
**Solution:** Make sure you loaded the LATEST `test-data.sql` file.

### Error: Connection refused on port 8080
**Solution:** Backend now runs on port **8081**, not 8080.

### Error: Column does not exist
**Solution:** Database schema mismatch. Check `DATABASE_STRUCTURE_NOTES.md`.

### Error: Cannot find module 'sockjs-client'
**Solution:** Run `npm install` in medCenter directory.

### Error: Chat messages have type "ADMIN"
**Solution:** Use type "OPERATOR" instead. ADMIN doesn't exist.

## 📚 Full Documentation

For detailed information, see:

- **`SUMMARY.md`** - Complete implementation overview
- **`CHEAT_SHEET.md`** - Quick command reference
- **`QUICK_START.md`** - Detailed setup guide
- **`DATABASE_STRUCTURE_NOTES.md`** - Database schema
- **`OPERATOR_VS_MANAGER.md`** - Role clarification

## 🎉 You're Ready!

After following the 3 steps above, you'll have:

✅ Backend running on port 8081
✅ Frontend running on port 4200
✅ Database with 5 patients, 5 doctors, 15 visits
✅ Working chat system with real-time messaging
✅ Operator interface for broadcast messages
✅ User interface with unread notifications
✅ Complete visit history API

**Open** http://localhost:4200 **and start testing!** 🚀

---

**Need help?** Check the documentation files or the error solutions above.
**Everything working?** You're all set! 🎊

