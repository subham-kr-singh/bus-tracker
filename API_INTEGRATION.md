# Bus Tracker Integration Guide: 3-App Architecture

This guide structures the integration for three separate front-end applications sharing the same Spring Boot backend.

**Base URL:** `https://bus-tracker-backend-production-1f1c.up.railway.app`
**WebSocket URL:** `wss://bus-tracker-backend-production-1f1c.up.railway.app/ws`

---

## 📱 1. Student App (Passenger)
**Target Users:** Students/Parents requiring bus location and ETA.

### **Core Flow**
1.  **Login:** User logs in (`POST /api/auth/login`) -> Validates role `STUDENT`.
2.  **Home/Map:**
    *   **Nearby Stops:** Call `GET /api/student/stops/nearby?lat={lat}&lng={lng}` to show stops around the user.
    *   **Select Stop:** User picks a stop to see incoming buses.
3.  **Bus Tracking:**
    *   **Get Buses:** Call `GET /api/student/morning-buses?stopId={id}&date={today}` to list buses headed to that stop.
    *   **Live Updates:** Connect to WebSocket `.../ws` and subscribe to `/topic/bus/{busId}` for the selected bus. Update map marker in real-time.

### **API Endpoints (Student)**
| Method | Endpoint | Purpose | Params |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | Login | `{email, password}` |
| **GET** | `/api/student/stops/nearby` | Find stops | `lat`, `lng`, `radius` |
| **GET** | `/api/student/morning-buses` | List buses for stop | `date`, `stopId` |

---

## 🚐 2. Driver App
**Target Users:** Bus Drivers needing to broadcast location and view schedule.

### **Core Flow**
1.  **Login:** User logs in (`POST /api/auth/login`) -> Validates role `DRIVER`.
2.  **Dashboard:**
    *   **My Schedule:** Call `GET /api/driver/schedules/today` to see assigned route/bus.
3.  **Active Trip:**
    *   **Start Trip:** Driver selects a schedule.
    *   **Location Broadcast:** App runs a background service/loop updating GPS every 5-10s via `POST /api/driver/location`.

### **API Endpoints (Driver)**
| Method | Endpoint | Purpose | Params |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | Login | `{email, password}` |
| **GET** | `/api/driver/schedules/today` | Get assigned trip | None (uses token) |
| **POST** | `/api/driver/location` | **Broadcast GPS** | `{scheduleId, lat, lng, speed}` |

---

## 💻 3. Admin Dashboard (Web/App)
**Target Users:** Transport Managers managing fleet, routes, and assignments.

### **Core Flow**
1.  **Login:** User logs in (`POST /api/auth/login`) -> Validates role `ADMIN`.
2.  **Fleet Management:**
    *   **View All:** Call `GET /api/admin/buses` to list all buses and status.
    *   **Add Bus:** Call `POST /api/admin/buses` to register new vehicles.
3.  **Route/Schedule Ops:**
    *   **Assign Bus:** Call `PUT /api/admin/schedules/{id}` to swap a bus (e.g., breakdown replacement).
4.  **Register Users (Optional):**
    *   **Add Users:** Call `POST /api/auth/register` to create accounts for Students or Drivers.

### **API Endpoints (Admin)**
| Method | Endpoint | Purpose | Params |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | Login | `{email, password}` |
| **POST** | `/api/auth/register` | Create New User | `{email, role, ...}` |
| **GET** | `/api/admin/buses` | List all buses | None |
| **POST** | `/api/admin/buses` | Add new bus | `{busNumber, capacity, ...}` |
| **PUT** | `/api/admin/schedules/{id}` | Assign Bus to Schedule | `{busId}` |

---

## 📡 WebSocket Integration (Real-Time)
**Used By:** Student App (Receive), Admin Dashboard (Monitor).

1.  **Library:** Use a STOMP client (e.g., `@stomp/stompjs` for React/React Native).
2.  **Connection:**
    ```javascript
    const client = new Client({
      brokerURL: 'wss://bus-tracker-backend-production-1f1c.up.railway.app/ws',
      onConnect: () => {
        // Subscribe to specific bus
        client.subscribe('/topic/bus/101', message => {
          const location = JSON.parse(message.body);
          console.log(`Bus 101 is at: ${location.latitude}, ${location.longitude}`);
        });
      }
    });
    client.activate();
    ```

---

## 🔒 Security Best Practices
*   **JWT Storage:**
    *   **Web (Admin):** Store in `HttpOnly` cookies (if possible) or `localStorage`.
    *   **Mobile (Student/Driver):** Store in `SecureStore` (Expo) or `Keychain` (React Native CLI).
*   **Token Expiry:** Handle `401 Unauthorized` by redirecting to Login screen.
