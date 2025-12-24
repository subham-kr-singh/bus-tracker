# Bus Tracker Backend API Integration Guide

This guide provides detailed instructions on how to integrate with the Bus Tracker backend API hosted on Railway.

**Base URL:** `https://bus-tracker-backend-production-1f1c.up.railway.app`
**WebSocket URL:** `wss://bus-tracker-backend-production-1f1c.up.railway.app/ws`

## Authentication

The API uses JWT (JSON Web Token) for authentication. All protected endpoints typically require the `Authorization` header.

**Header Format:** 
`Authorization: Bearer <your_jwt_token>`

### 1. Login
**Endpoint:** `POST /api/auth/login`
**Public Access:** Yes

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "yourpassword"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ADMIN" // or "DRIVER", "STUDENT"
}
```
*Note: Store the `accessToken` and `role` securely on the client side.*

### 2. Register (Admin Only)
**Endpoint:** `POST /api/auth/register`
**Role Required:** `ADMIN`

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "passwordHash": "securepassword", 
  "role": "STUDENT", 
  "name": "John Doe",
  "phone": "1234567890"
}
```
*Note: `passwordHash` in request is the raw password; backend will hash it.*

---

## Student API
**Role Required:** `STUDENT`

### 1. Get Nearby Stops
**Endpoint:** `GET /api/student/stops/nearby`

**Query Parameters:**
* `lat`: Current latitude (Double)
* `lng`: Current longitude (Double)
* `radius`: Search radius in meters (Double, default: 1000)

**Request Example:**
`/api/student/stops/nearby?lat=28.6139&lng=77.2090&radius=1500`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Main Gate",
    "latitude": 28.6145,
    "longitude": 77.2095,
    "distance": 150.5
  },
  ...
]
```

### 2. Get Morning Buses for a Stop
**Endpoint:** `GET /api/student/morning-buses`

**Query Parameters:**
* `date`: Date in ISO format (YYYY-MM-DD)
* `stopId`: ID of the bus stop (Long)

**Request Example:**
`/api/student/morning-buses?date=2025-12-25&stopId=1`

**Response:**
```json
[
  {
    "busId": 101,
    "busNumber": "DL-1PC-1234",
    "routeName": "Route A",
    "etaMinutes": 12,
    "latitude": 28.6100,
    "longitude": 77.2000
  },
  ...
]
```

---

## Driver API
**Role Required:** `DRIVER`

### 1. Get Today's Schedules
**Endpoint:** `GET /api/driver/schedules/today`

**Response:**
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

### 2. Update Bus Location
**Endpoint:** `POST /api/driver/location`

**Request Body:**
```json
{
  "scheduleId": 501,
  "latitude": 28.6150,
  "longitude": 77.2080,
  "speed": 45.5
}
```

**Response:** `200 OK` ("Location updated")

---

## Admin API
**Role Required:** `ADMIN`

### 1. Get All Buses
**Endpoint:** `GET /api/admin/buses`

**Response:**
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

### 2. Create Bus
**Endpoint:** `POST /api/admin/buses`

**Request Body:**
```json
{
  "busNumber": "DL-1PC-5678",
  "capacity": 40,
  "gpsDeviceId": "GPS-002",
  "status": "MAINTENANCE"
}
```

### 3. Update Daily Schedule (Assign Bus)
**Endpoint:** `PUT /api/admin/schedules/{id}`
**Path Variable:** `id` (Schedule ID)

**Request Body:**
```json
{
  "busId": 105
}
```

---

## Real-time Updates (WebSocket)

The application uses STOMP over WebSocket for real-time bus location updates.

1.  **Connect:**
    Connect to `wss://bus-tracker-backend-production-1f1c.up.railway.app/ws`

2.  **Subscribe:**
    Subscribe to `/topic/bus/{busId}` to receive location updates for a specific bus.

    **Example:** `/topic/bus/101`

3.  **Incoming Message Format:**
    The payload received on the subscribed topic will be a JSON string matching the `LocationUpdateDto`, often simplified or mapped directly from the driver's update payload.

    ```json
    {
      "scheduleId": 501,
      "latitude": 28.6155,
      "longitude": 77.2085,
      "speed": 48.0
    }
    ```

---

## Application Structure & Flow

1.  **Authentication Flow:**
    *   User logs in via `/api/auth/login`.
    *   Client receives JWT and role.
    *   Client stores JWT and restricts UI based on role.

2.  **Student Flow:**
    *   Student uses GPS to find nearby stops (`/api/student/stops/nearby`).
    *   Selects a stop and views incoming buses (`/api/student/morning-buses`).
    *   App connects to WebSocket `/topic/bus/{busId}` for the selected bus to show live movement on a map.

3.  **Driver Flow:**
    *   Driver logs in and sees their schedule (`/api/driver/schedules/today`).
    *   Driver starts a trip.
    *   Phone's GPS periodically (e.g., every 5-10s) sends updates to `/api/driver/location`.

4.  **Admin Flow:**
    *   Admin manages buses, routes, and daily schedules via the Admin API.
    *   Can re-assign buses to schedules dynamically if a bus breaks down.

## Error Handling
Standard HTTP status codes are used:
*   `200 OK`: Success
*   `401 Unauthorized`: Invalid or missing token
*   `403 Forbidden`: Insufficient permissions (role mismatch)
*   `404 Not Found`: Resource not found
*   `500 Internal Server Error`: Server-side processing error (check server logs)
