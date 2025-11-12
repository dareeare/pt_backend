# UUID Format Fix

## ❌ Problem

UUID format error when loading test data:

```
ERROR: invalid input syntax for type uuid: "vvvvvvvv-0001-0001-0001-000000000001"
```

## 🔍 Root Cause

UUID can only contain **hexadecimal characters** (0-9, a-f).

Invalid characters used:
- `v` - NOT a hex character ❌
- `g` - NOT a hex character ❌  
- `s`, `c`, `h`, `d` in "schd" - some invalid ❌
- `r`, `e`, `v` in "rev" - 'v' is invalid ❌
- `m`, `s`, `g` in "msg" - 's', 'g' invalid ❌

## ✅ Solution

Changed all UUIDs to use only valid hex characters (0-9, a-f):

### Visit IDs:
```sql
-- BEFORE (invalid):
'vvvvvvvv-0001-0001-0001-000000000001'  -- ❌

-- AFTER (valid):
'd0000001-0001-0001-0001-000000000001'  -- ✅ 'd' is valid hex
'd0000002-0002-0002-0002-000000000002'  -- ✅
...
'e0000007-1007-1007-1007-000000001007'  -- ✅ 'e' is valid hex
```

### Schedule IDs:
```sql
-- BEFORE:
'schd-0001-0001-0001-000000000001'      -- ❌ 's', 'c', 'h' not hex

-- AFTER:
'b0000001-0001-0001-0001-000000000001'  -- ✅ 'b' is valid hex
'b0000002-0002-0002-0002-000000000002'  -- ✅
```

### Review IDs:
```sql
-- BEFORE:
'rev-0001-0001-0001-000000000001'       -- ❌ 'v' not hex

-- AFTER:
'c0000001-0001-0001-0001-000000000001'  -- ✅ 'c' is valid hex
```

### Service IDs:
```sql
-- BEFORE:
'ssssssss-1111-1111-1111-111111111111'  -- ❌ 's' is valid but repeated 8 times

-- AFTER:
'a0000001-0000-0000-0000-000000000001'  -- ✅ 'a' is valid hex
'a0000002-0000-0000-0000-000000000002'  -- ✅
```

### Chat Message IDs:
```sql
-- BEFORE:
'msg-0001-0001-0001-000000000001'       -- ❌ 'm', 's', 'g' not all hex

-- AFTER:
'f0000001-0001-0001-0001-000000000001'  -- ✅ 'f' is valid hex
```

### Operator/Manager IDs:
```sql
-- Operator:
'00000000-0000-0000-0000-000000000010'  -- ✅ all zeros valid

-- Manager:
'f0000000-0000-0000-0000-000000000020'  -- ✅ 'f' is valid hex
```

## 📝 UUID Prefixes Used

For easy identification while keeping valid hex format:

| Entity | Prefix | Example |
|--------|--------|---------|
| Past Visit | `d00000XX` | `d0000001-0001-0001-0001-000000000001` |
| Future Visit | `e00000XX` | `e0000001-1001-1001-1001-000000001001` |
| Schedule | `b00000XX` | `b0000001-0001-0001-0001-000000000001` |
| Review | `c00000XX` | `c0000001-0001-0001-0001-000000000001` |
| Service | `a00000XX` | `a0000001-0000-0000-0000-000000000001` |
| Chat Message | `f00000XX` | `f0000001-0001-0001-0001-000000000001` |
| Patient | `11111111` to `55555555` | `11111111-1111-1111-1111-111111111111` |
| Doctor | `aaaaaaaa` to `eeeeeeee` | `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa` |
| Operator | `00000000` | `00000000-0000-0000-0000-000000000010` |
| Manager | `f0000000` | `f0000000-0000-0000-0000-000000000020` |

All prefixes use valid hexadecimal characters: 0-9, a-f

## ✅ Validation

Valid hex digits in UUID: **0 1 2 3 4 5 6 7 8 9 a b c d e f**

Invalid characters: ~~g h i j k l m n o p q r s t u v w x y z~~

## 🔄 How to Apply

The corrected `test-data.sql` file now uses only valid UUIDs.

```bash
# Clear old data
psql -h localhost -p 5433 -U postgres -d medicalcenter -c "TRUNCATE TABLE chat_messages, doctorreviews, visit, schedule, service, manager, operators, doctor, patient CASCADE;"

# Load corrected data
psql -h localhost -p 5433 -U postgres -d medicalcenter -f "D:\medBack\UserService\src\main\resources\test-data.sql"
```

Should load without errors now! ✅

## 🎓 Why This Matters

PostgreSQL's UUID type strictly validates that all characters in a UUID are valid hexadecimal digits. Using descriptive prefixes like "msg-" or "rev-" makes the data more readable but causes validation errors.

Solution: Use hex-only prefixes that are still somewhat meaningful:
- `a` = service
- `b` = schedule
- `c` = review (c for "comment")
- `d` = past visit (d for "done")
- `e` = future visit (e for "expected")
- `f` = message (f for "final" hex digit)

