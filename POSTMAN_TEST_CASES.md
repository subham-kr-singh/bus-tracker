# Bus Tracker API - Postman Test Cases

## Setup Instructions
1. Import `Bus-Tracker-API.postman_collection.json` into Postman
2. Create an Environment named "Bus Tracker - Production"
3. Add variables:
   - `base_url`: `https://bus-tracker-backend-production-1f1c.up.railway.app/api`
   - `jwt_token`: (will be auto-populated after login)

---

## Test Suite 1: Health & Infrastructure

### TC-001: Server Health Check
**Endpoint:** `GET /actuator/health`  
**Auth:** None  
**Expected Response:** `200 OK`
```json
{
  "status": "UP"
}
```
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Application is UP", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.eql("UP");
});
```

---

## Test Suite 2: Authentication Flow

### TC-002: Login with Valid Credentials
**Endpoint:** `POST /auth/login`  
**Auth:** None  
**Request Body:**
```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```
**Expected Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUz...",
  "role": "ADMIN"
}
```
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has token and role", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('accessToken');
    pm.expect(jsonData).to.have.property('role');
    
    // Save token for subsequent requests
    pm.environment.set("jwt_token", jsonData.accessToken);
    pm.collectionVariables.set("jwt_token", jsonData.accessToken);
});

pm.test("Token is not empty", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.accessToken).to.not.be.empty;
});

pm.test("Role is valid", function () {
    var jsonData = pm.response.json();
    pm.expect(['ADMIN', 'DRIVER', 'STUDENT']).to.include(jsonData.role);
});
```

### TC-003: Login with Invalid Credentials
**Endpoint:** `POST /auth/login`  
**Request Body:**
```json
{
  "email": "wrong@example.com",
  "password": "wrongpass"
}
```
**Expected Response:** `401 Unauthorized` or `400 Bad Request`
**Postman Tests:**
```javascript
pm.test("Status code is 401 or 400", function () {
    pm.expect([401, 400]).to.include(pm.response.code);
});

pm.test("Response does not contain token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.not.have.property('accessToken');
});
```

### TC-004: Register New User (Admin Only)
**Endpoint:** `POST /auth/register`  
**Auth:** Bearer Token (ADMIN)  
**Request Body:**
```json
{
  "email": "test_{{$timestamp}}@example.com",
  "passwordHash": "password123",
  "role": "STUDENT",
  "name": "Test User",
  "phone": "1234567890"
}
```
**Expected Response:** `200 OK` or `201 Created`
**Postman Tests:**
```javascript
pm.test("Status code is 200 or 201", function () {
    pm.expect([200, 201]).to.include(pm.response.code);
});

pm.test("User created successfully", function () {
    pm.response.to.have.status(200);
});
```

---

## Test Suite 3: Student API

### TC-005: Get Nearby Stops
**Endpoint:** `GET /student/stops/nearby?lat=28.6139&lng=77.2090&radius=1500`  
**Auth:** Bearer Token (STUDENT/ADMIN)  
**Expected Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Main Gate",
    "latitude": 28.6145,
    "longitude": 77.2095,
    "distance": 150.5
  }
]
```
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Each stop has required fields", function () {
    var stops = pm.response.json();
    if (stops.length > 0) {
        pm.expect(stops[0]).to.have.property('id');
        pm.expect(stops[0]).to.have.property('name');
        pm.expect(stops[0]).to.have.property('latitude');
        pm.expect(stops[0]).to.have.property('longitude');
        pm.expect(stops[0]).to.have.property('distance');
    }
});

pm.test("Stops are ordered by distance", function () {
    var stops = pm.response.json();
    for (var i = 0; i < stops.length - 1; i++) {
        pm.expect(stops[i].distance).to.be.at.most(stops[i + 1].distance);
    }
});
```

### TC-006: Get Morning Buses
**Endpoint:** `GET /student/morning-buses?date=2025-12-25&stopId=1`  
**Auth:** Bearer Token (STUDENT/ADMIN)  
**Expected Response:** `200 OK`
```json
[
  {
    "busId": 101,
    "busNumber": "DL-1PC-1234",
    "routeName": "Route A",
    "etaMinutes": 12,
    "latitude": 28.6100,
    "longitude": 77.2000
  }
]
```
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Bus details are complete", function () {
    var buses = pm.response.json();
    if (buses.length > 0) {
        pm.expect(buses[0]).to.have.property('busId');
        pm.expect(buses[0]).to.have.property('busNumber');
        pm.expect(buses[0]).to.have.property('routeName');
        pm.expect(buses[0]).to.have.property('etaMinutes');
    }
});
```

---

## Test Suite 4: Driver API

### TC-007: Get Driver's Today Schedules
**Endpoint:** `GET /driver/schedules/today`  
**Auth:** Bearer Token (DRIVER)  
**Expected Response:** `200 OK`
```json
[
  {
    "id": 501,
    "busNumber": "DL-1PC-1234",
    "routeName": "Route A",
    "direction": "MORNING"
  }
]
```
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Schedule has required fields", function () {
    var schedules = pm.response.json();
    if (schedules.length > 0) {
        pm.expect(schedules[0]).to.have.property('id');
        pm.expect(schedules[0]).to.have.property('busNumber');
        pm.expect(schedules[0]).to.have.property('routeName');
        pm.expect(schedules[0]).to.have.property('direction');
    }
});
```

### TC-008: Update Bus Location
**Endpoint:** `POST /driver/location`  
**Auth:** Bearer Token (DRIVER)  
**Request Body:**
```json
{
  "scheduleId": 501,
  "latitude": 28.6150,
  "longitude": 77.2080,
  "speed": 45.5
}
```
**Expected Response:** `200 OK`
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

### TC-009: Update Location with Invalid Data
**Endpoint:** `POST /driver/location`  
**Request Body:**
```json
{
  "scheduleId": -1,
  "latitude": 200,
  "longitude": 300,
  "speed": -10
}
```
**Expected Response:** `400 Bad Request`
**Postman Tests:**
```javascript
pm.test("Status code is 400", function () {
    pm.response.to.have.status(400);
});
```

---

## Test Suite 5: Admin API

### TC-010: Get All Buses
**Endpoint:** `GET /admin/buses`  
**Auth:** Bearer Token (ADMIN)  
**Expected Response:** `200 OK`
```json
[
  {
    "id": 101,
    "busNumber": "DL-1PC-1234",
    "capacity": 50,
    "gpsDeviceId": "GPS-001",
    "status": "ACTIVE"
  }
]
```
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response is an array", function () {
    pm.expect(pm.response.json()).to.be.an('array');
});

pm.test("Bus has required fields", function () {
    var buses = pm.response.json();
    if (buses.length > 0) {
        pm.expect(buses[0]).to.have.property('id');
        pm.expect(buses[0]).to.have.property('busNumber');
        pm.expect(buses[0]).to.have.property('capacity');
        pm.expect(buses[0]).to.have.property('status');
    }
});
```

### TC-011: Create New Bus
**Endpoint:** `POST /admin/buses`  
**Auth:** Bearer Token (ADMIN)  
**Request Body:**
```json
{
  "busNumber": "TEST-{{$timestamp}}",
  "capacity": 40,
  "gpsDeviceId": "GPS-{{$timestamp}}",
  "status": "ACTIVE"
}
```
**Expected Response:** `200 OK` or `201 Created`
**Postman Tests:**
```javascript
pm.test("Status code is 200 or 201", function () {
    pm.expect([200, 201]).to.include(pm.response.code);
});

pm.test("Response contains bus ID", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
});
```

### TC-012: Update Schedule
**Endpoint:** `PUT /admin/schedules/1`  
**Auth:** Bearer Token (ADMIN)  
**Request Body:**
```json
{
  "busId": 105
}
```
**Expected Response:** `200 OK`
**Postman Tests:**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

---

## Test Suite 6: Authorization & Security

### TC-013: Access Admin Endpoint without Token
**Endpoint:** `GET /admin/buses`  
**Auth:** None  
**Expected Response:** `401 Unauthorized`
**Postman Tests:**
```javascript
pm.test("Status code is 401", function () {
    pm.response.to.have.status(401);
});
```

### TC-014: Access Admin Endpoint with Student Token
**Endpoint:** `GET /admin/buses`  
**Auth:** Bearer Token (STUDENT)  
**Expected Response:** `403 Forbidden`
**Postman Tests:**
```javascript
pm.test("Status code is 403", function () {
    pm.response.to.have.status(403);
});
```

---

## Test Suite 7: Performance & Load

### TC-015: Response Time Check
**Applies to:** All GET endpoints  
**Expected:** Response time < 500ms for simple queries
**Postman Tests:**
```javascript
pm.test("Response time is acceptable", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

### TC-016: Concurrent Location Updates
**Endpoint:** `POST /driver/location`  
**Test:** Send 10 concurrent requests  
**Expected:** All succeed with 200 OK  
**Note:** Use Postman Collection Runner with 10 iterations

---

## Running Tests

### 1. Manual Testing
- Run tests individually to verify functionality
- Check all assertions pass

### 2. Collection Runner
- Select "Bus Tracker API" collection
- Click "Run" → "Run Bus Tracker API"
- Set iterations: 1
- Enable "Save responses"
- Click "Run Bus Tracker API"

### 3. Newman (CLI)
```bash
npm install -g newman
newman run Bus-Tracker-API.postman_collection.json -e production.postman_environment.json
```

---

## Expected Test Results Summary

| Test Suite | Total Tests | Pass | Fail |
|------------|-------------|------|------|
| Health & Infrastructure | 1 | 1 | 0 |
| Authentication | 3 | 3 | 0 |
| Student API | 2 | 2 | 0 |
| Driver API | 3 | 3 | 0 |
| Admin API | 3 | 3 | 0 |
| Security | 2 | 2 | 0 |
| Performance | 2 | 2 | 0 |
| **Total** | **16** | **16** | **0** |

---

## Notes
- Replace `admin@example.com` and `password123` with actual credentials
- Some tests require database seeding with sample data
- WebSocket tests are not included (use dedicated WebSocket testing tools)
