# Operator vs Manager - Architecture Update

## 📋 Role Clarification

Based on the database structure and MessageType enum, the system has been updated:

### User Roles:

1. **Patient (User)** 
   - Can view their own visit history
   - Can send messages in chat with type: `USER`
   - Route: `/user`
   - Chat route: `/chat-user`

2. **Operator** (formerly called Admin/Manager in UI)
   - Can send broadcast messages to all users
   - Messages have type: `OPERATOR`
   - Route: `/operator`
   - Chat route: `/chat-operator`
   - Database table: `operators`

3. **Manager**
   - Exists in database (`manager` table)
   - Currently no separate UI (can use operator interface)
   - Administrative role for system management

4. **Doctor**
   - Can view their own schedule and visit history
   - Can view patient history for preparation
   - Route: `/doctor`

5. **System**
   - Automated system messages
   - Type: `SYSTEM`
   - No user interface (backend only)

## 🔄 Changes Made

### Frontend Components Renamed:

| Old Name | New Name | Purpose |
|----------|----------|---------|
| `manager-start` | `operator-start` | Operator's home screen |
| `chat-admin` | `chat-operator` | Operator's chat interface |
| `chat-user` | `chat-user` | User's chat interface (no change) |

### Routes Updated:

```typescript
// Old routes (removed):
// { path: 'manager', component: ManagerStart }
// { path: 'chat-admin', component: ChatAdmin }

// New routes:
{ path: 'operator', component: OperatorStart }
{ path: 'chat-operator', component: ChatOperator }
{ path: 'chat-user', component: ChatUser }
```

### MessageType Enum Values:

```java
public enum MessageType {
    USER,      // Messages from patients
    OPERATOR,  // Messages from call center operators
    SYSTEM     // Automated system messages
}
```

**Note:** `ADMIN` does NOT exist in the enum!

## 🎯 Use Cases

### Operator Workflow:

1. Operator logs in → navigates to `/operator`
2. Sees main screen with:
   - Profile settings
   - Services management
   - **Chat button** (main feature)
3. Clicks Chat → goes to `/chat-operator`
4. Can send broadcast messages to all users
5. Messages are saved with type: `OPERATOR`
6. All connected users receive messages in real-time

### Patient Workflow:

1. Patient logs in → navigates to `/user`
2. Sees main screen with:
   - Profile settings
   - Service archive (visit history)
   - **Chat button** with unread badge
3. Clicks Chat → goes to `/chat-user`
4. Can send messages (type: `USER`)
5. Receives operator responses in real-time
6. Red badge appears when new messages arrive
7. Badge clears when chat is opened

## 🎨 UI Screens

### Screen 1: Operator Home (`/operator`)
- Header with operator name and avatar
- Profile settings card
- Services card
- **Chat card** (highlighted with gradient)

### Screen 2: Operator Chat (`/chat-operator`)
- Header: "Medical center" + "Feel Your Best. Book Your Visit Today!"
- Online users count
- **"Operator" label** in header
- Message history
- Input field for broadcast messages
- Send button

### Screen 3: Patient Chat (`/chat-user`)
- Header: "Medical center" + "Feel Your Best. Book Your Visit Today!"
- Message history (own messages on right, operator on left)
- Input field for messages
- Send button

### Screen 4: Patient Home (`/user`)
- Same as before
- **Chat button with red badge** when unread messages exist
- Badge shows count of unread messages
- Badge disappears when chat is opened

## 📊 Database Mapping

### operators table:
```sql
operator_id    | John Operator (UUID: 00000000-0000-0000-0000-000000000010)
first_name     | John
last_name      | Operator
email          | john.operator@medcenter.com
```

### chat_messages table:
```sql
sender_id      | 00000000-0000-0000-0000-000000000010 (operator's UUID)
sender_name    | "John Operator"
type           | 'OPERATOR'
content        | "Hello! You can book through our website..."
```

## 🔧 Technical Implementation

### Component Naming Convention:

```typescript
// Operator components:
export class OperatorStart { }      // operator-start.ts
export class ChatOperator { }       // chat-operator.ts

// User components:
export class UserStart { }          // user-start.ts
export class ChatUser { }           // chat-user.ts
```

### Selectors:

```typescript
@Component({
  selector: 'app-operator-start',  // Operator home
  selector: 'app-chat-operator',   // Operator chat
  selector: 'app-chat-user',       // User chat
})
```

### Message Types in Code:

```typescript
// When operator sends message:
type: 'OPERATOR'

// When user sends message:
type: 'USER'

// System automated messages:
type: 'SYSTEM'
```

## 📱 Access URLs

- **Operator home**: http://localhost:4200/operator
- **Operator chat**: http://localhost:4200/chat-operator
- **User home**: http://localhost:4200/user
- **User chat**: http://localhost:4200/chat-user

## ✅ Consistency Check

All references updated:
- ✅ Frontend components renamed
- ✅ Routes updated
- ✅ MessageType uses OPERATOR (not ADMIN)
- ✅ Database test data uses OPERATOR type
- ✅ Documentation updated
- ✅ CSS class names updated (`.operator` instead of `.admin`)

## 🎯 Why This Change?

**Before:** Confused naming (Manager/Admin for chat operators)
**After:** Clear role separation:
- **Manager** = administrative role (database management)
- **Operator** = call center/support role (chat with users)
- **Patient** = end user
- **Doctor** = medical staff

This matches the database structure where `operators` table represents call center staff who handle patient communications.

---

**Updated:** November 9, 2025
**Status:** ✅ All components renamed and functional

