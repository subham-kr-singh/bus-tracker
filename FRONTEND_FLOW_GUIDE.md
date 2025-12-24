# 📱 Bus Tracker Frontend Guide: Architecture & Flow

This comprehensive guide details the structure, flow, and integration of the three React Native Expo applications: **Student App**, **Driver App**, and **Admin Dashboard**.

---

## 🏗️ 1. Global Project Structure
We recommend a mono-repo style or 3 separate repos, but sharing a common internal logic structure.

### Recommended Folder Structure (Per App)
```text
/src
  ├── /api
  │     ├── client.js          # Axios instance with Interceptors
  │     └── specificApis.js    # Feature-specific calls
  ├── /auth
  │     ├── AuthContext.js     # Global State (Token, userRole)
  │     └── storage.js         # SecureStore helper
  ├── /components              # Reusable UI (Buttons, Cards)
  ├── /navigation              # React Navigation Stacks
  │     ├── AppNavigator.js
  │     └── AuthNavigator.js
  ├── /screens                 # Application Screens
  ├── /utils
  │     ├── socket.js          # WebSocket Logic    
  │     └── constants.js       # Config values
  └── App.js                   # Entry Point
```

---

## 🔐 2. Authentication & Connection Flow

All three apps share the **Same Backend** but have different entry flows.

### Shared Connection Logic (`/api/client.js`)
```javascript
import axios from 'axios';
import * as SecureStore from 'expo-secure-store';

const api = axios.create({
  baseURL: 'https://bus-tracker-backend-production-1f1c.up.railway.app/api'
});

// Auto-attach JWT Token
api.interceptors.request.use(async (config) => {
  const token = await SecureStore.getItemAsync('jwt_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;
```

---

## 🎓 3. Student App (Passenger)

### **Flow:**
1.  **Splash Screen** -> Check if Token exists.
2.  **Login Screen** -> User enters credentials.
    *   API: `POST /auth/login`
    *   Logic: If `role !== 'STUDENT'`, show error "Access Denied".
3.  **Home (Map) Screen**
    *   **Action**: Fetch nearby stops based on GPS.
    *   **API**: `GET /student/stops/nearby?lat=...&lng=...`
4.  **Bus Tracking Screen**
    *   **Action**: Tap a stop -> See incoming buses.
    *   **Real-time**: Subscribe to WebSocket `/topic/bus/{id}`.
    *   **Visual**: Update Bus Marker on Map every second.

### **Screens Folder:**
*   `LoginScreen.js`
*   `NearbyStopsMap.js`
*   `BusTrackingScreen.js`
*   `ProfileScreen.js`

---

## 🚐 4. Driver App (Broadcaster)

### **Flow:**
1.  **Login Screen** -> Login.
    *   API: `POST /auth/login`
    *   Logic: If `role !== 'DRIVER'`, block access.
2.  **Dashboard (Schedule)**
    *   **Action**: View today's route.
    *   **API**: `GET /driver/schedules/today`
3.  **Active Trip Screen**
    *   **Action**: "Start Trip" button.
    *   **Background Task**:
        *   Every 5 seconds: Get GPS location.
        *   API: `POST /driver/location`
        *   *Crucial*: Must work even if screen is off (Use `expo-location` background services).

### **Screens Folder:**
*   `DriverLogin.js`
*   `ScheduleList.js`
*   `ActiveTripMap.js`

---

## 💻 5. Admin Dashboard (Web/Tablet)

### **Flow:**
1.  **Login Screen** -> Admin credentials.
2.  **Dashboard Overview**
    *   Stats: Active buses, Total Users.
3.  **Fleet Management**
    *   **List**: `GET /admin/buses`
    *   **Add**: `POST /admin/buses`
4.  **Schedule Management**
    *   **Action**: Assign Driver/Bus to Route.
    *   **API**: `PUT /admin/schedules/{id}`

### **Screens Folder:**
*   `AdminLogin.js`
*   `DashboardOverview.js`
*   `BusManagement.js`
*   `UserManagement.js` (Register new Drivers/Students)

---

## 🔌 6. WebSocket Integration (The "Magic" Part)
How frontends connect live:

**File:** `/src/utils/socket.js`
```javascript
import { Client } from '@stomp/stompjs';

const connectWebSocket = (onMessageReceived) => {
  const client = new Client({
    brokerURL: 'wss://bus-tracker-backend-production-1f1c.up.railway.app/ws',
    reconnectDelay: 5000,
  });

  client.onConnect = () => {
    // Subscribe to specific car/bus updates
    client.subscribe('/topic/bus/101', message => {
        onMessageReceived(JSON.parse(message.body));
    });
  };

  client.activate();
  return client;
};
```

---

## 🚀 Summary Checklist for Developers

1.  [ ] **Repo Setup**: Create 3 repos using `npx create-expo-app`.
2.  [ ] **Env Vars**: Add API URL to `.env`.
3.  [ ] **Auth**: Implement Login logic storing JWT in `SecureStore`.
4.  [ ] **Maps**: Integrate `react-native-maps` (Google Maps API Key required for Android).
5.  [ ] **Test**: Use the Postman Collection user credentials to test log in.
