# API Health Report
**Generated:** 2025-12-24 14:20 IST  
**Backend URL:** https://bus-tracker-backend-production-1f1c.up.railway.app

---

## 🔴 CRITICAL: Application Status
**Status:** `DOWN` (502 Bad Gateway)

### Test Results

| Endpoint | Method | Expected | Actual | Status |
|----------|--------|----------|--------|--------|
| `/actuator/health` | GET | 200 OK | **502 Bad Gateway** | ❌ FAILED |
| `/actuator/info` | GET | 200 OK | **502 Bad Gateway** | ❌ FAILED |
| `/api/auth/login` | POST | 200 OK | **Not Tested** | ⏸️ BLOCKED |
| `/api/admin/buses` | GET | 200/401 | **Not Tested** | ⏸️ BLOCKED |
| `/api/student/stops/nearby` | GET | 200/401 | **Not Tested** | ⏸️ BLOCKED |
| `/api/driver/schedules/today` | GET | 200/401 | **Not Tested** | ⏸️ BLOCKED |

---

## 🔍 Root Cause Analysis

A **502 Bad Gateway** from Railway typically indicates:

### 1. Application Crash (Most Likely)
- ✅ **Java Memory Issue**: The recent optimizations added new dependencies (Resilience4j).
  - **Maven Build Failed**: New dependencies may require rebuilding.
  - **JAR Not Found**: Railway couldn't locate `target/*.jar`.

### 2. Database Connection Failure
- ❌ **Supabase Timeout**: PostgreSQL connection at startup might be timing out.
- ❌ **Redis Connection Failure**: Upstash Redis might be unreachable or credentials expired.

### 3. Port Binding Issue
- ❌ **PORT Environment Variable**: Railway expects the app to bind to `$PORT`, but app might be hardcoded to 8080.

---

## 🛠️ Immediate Actions Required

### Step 1: Check Railway Logs (CRITICAL)
Go to **Railway Dashboard → Your Service → Deployments → Latest → View Logs**

**Look for these patterns:**

```
❌ BAD SIGNS:
- "Error creating bean"
- "Failed to configure a DataSource"
- "Connection refused" (Redis/PostgreSQL)
- "java.lang.OutOfMemoryError"
- "BUILD FAILED"

✅ GOOD SIGNS:
- "Started BusTrackerApplication in X seconds"
- "Tomcat started on port"
- "HikariPool-1 - Start completed"
```

### Step 2: Fix Build Issues
If build is failing, the problem is likely **missing dependencies** after adding Resilience4j.

**Verify `pom.xml`:**
```bash
cd c:\Users\ASUS\Documents\bus-tracker
mvn clean package -DskipTests
```

If this fails locally, the Railway build will also fail.

### Step 3: Check Environment Variables
Railway → Settings → Variables:

| Variable | Required | Notes |
|----------|----------|-------|
| `SPRING_DATASOURCE_URL` | ✅ | Must include `?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | ✅ | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | ✅ | Your Supabase password |
| `SPRING_DATA_REDIS_URL` | ✅ | Full Upstash URL |
| `PORT` | ✅ | Railway auto-sets this |

### Step 4: Redeploy
If logs show old code is still running:
1. Railway → Deployments → **Redeploy**
2. Wait for "Started" in logs (may take 30-60 seconds)

---

## 📊 Expected Health Report (When Fixed)

Once the app is UP, expect:

```http
GET /actuator/health
Response: 200 OK
{
  "status": "UP",
  "components": {
    "diskSpace": { "status": "UP" },
    "db": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

### API Endpoint Health (With Valid Token)

| Category | Endpoint | Expected Behavior |
|----------|----------|-------------------|
| **Auth** | `POST /api/auth/login` | Returns `accessToken` and `role` |
| **Student** | `GET /api/student/stops/nearby` | Returns nearby stops array |
| **Driver** | `GET /api/driver/schedules/today` | Returns schedule for logged-in driver |
| **Admin** | `GET /api/admin/buses` | Returns all buses |

---

## 🚨 Next Steps

1. **Check Railway Logs** immediately (see Step 1)
2. **Share the exact error** from logs
3. If BUILD FAILED:
   - Run `mvn clean install` locally
   - Check if Java 17 is available on Railway
4. If DATABASE error:
   - Verify Supabase credentials
   - Test connection: `psql "postgresql://postgres:PASSWORD@db.herratmohdcmsyhgimpl.supabase.co:5432/postgres"`

---

## 📝 How to Use `api-health-check.http`

Once the backend is UP:

1. Open `api-health-check.http` in VS Code
2. Install **REST Client** extension
3. Click "Send Request" on Test 1.1 (Health Check)
4. If ✅, proceed to Test 2.1 (Login)
5. Copy the `accessToken` from response
6. Replace `@token = ` at the top with your token
7. Run all authenticated tests

**Current Blocker:** App must be UP first (502 → 200).
