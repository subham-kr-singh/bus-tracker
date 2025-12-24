# Frontend Development Guide (Expo React Native)

This guide provides step-by-step instructions to set up and develop the three mobile applications (Student, Driver, Admin) using **React Native with Expo**.

## 🛠️ Prerequisites
1.  **Node.js** (LTS version) installed.
2.  **Expo CLI**: `npm install -g expo-cli`
3.  **Expo Go App**: Installed on your physical Android/iOS device for testing.

---

## 🚀 1. Project Initialization

We will create three separate Expo projects for distinct user roles.

### Step 1: Create the Apps
Open your terminal in your root development folder and run:

```bash
# 1. Student App (Passenger)
npx create-expo-app student-tracker --template blank
cd student-tracker
npm install

# 2. Driver App (GPS Broadcaster)
cd ..
npx create-expo-app driver-tracker --template blank
cd driver-tracker
npm install

# 3. Admin App (Dashboard)
cd ..
npx create-expo-app admin-dashboard --template blank
cd admin-dashboard
npm install
```

### Step 2: Install Common Dependencies
Each app will need these core libraries for Navigation, Maps, Storage, and API calls. Run this inside **EACH** project folder:

```bash
# Navigation (React Navigation)
npm install @react-navigation/native @react-navigation/stack
npx expo install react-native-screens react-native-safe-area-context

# Maps (React Native Maps)
npx expo install react-native-maps

# Location (GPS) - Critical for Student & Driver
npx expo install expo-location

# Storage (JWT Tokens)
npx expo install expo-secure-store

# WebSocket (Real-time connection)
npm install @stomp/stompjs text-encoding
# Note: standard WebSocket API is built-in to React Native

# Vector Icons (UI)
npx expo install @expo/vector-icons
```

---

## 📂 2. Recommended Project Structure

For all three apps, maintain a clean architecture:

```text
/src
  ├── /api
  │     ├── apiClient.js       # Axios instance with Interceptors
  │     ├── authApi.js         # Login/Register endpoints
  │     └── ...                # Feature specific APIs
  ├── /components
  │     ├── CustomButton.js    # Reusable UI
  │     └── MapView.js         # Wrapper for Maps
  ├── /context
  │     └── AuthContext.js     # State for Token & User Role
  ├── /navigation
  │     ├── AppNavigator.js    # Stack/Tab Navigators
  │     └── AuthNavigator.js   # Login Screens
  ├── /screens
  │     ├── LoginScreen.js
  │     └── HomeScreen.js
  └── /utils
        └── socket.js          # WebSocket helper
```

---

## ⚙️ 3. Environment Setup

Create a `.env` file (or `config.js`) in `src/utils/config.js` to store your backend URL.

```javascript
// src/utils/config.js
export const API_BASE_URL = "https://bus-tracker-backend-production-1f1c.up.railway.app/api";
export const WS_URL = "wss://bus-tracker-backend-production-1f1c.up.railway.app/ws";
```

---

## 📱 4. Implementation Steps by App

### A. Student App Implementation
**Goal**: View nearby buses and estimate arrival.

1.  **Auth**: Implement Login Screen calling `POST /api/auth/login`. Store JWT in `SecureStore`.
2.  **Map Screen**:
    *   Use `expo-location` to get user's current lat/lng.
    *   Call `GET /api/student/stops/nearby` on load.
    *   Render markers for each stop.
3.  **Bus Tracking**:
    *   When a stop is clicked, call `GET /api/student/morning-buses`.
    *   Connect WebSocket using `@stomp/stompjs`:
        ```javascript
        client.subscribe(`/topic/bus/${busId}`, (msg) => {
           const { latitude, longitude } = JSON.parse(msg.body);
           updateBusMarker(latitude, longitude);
        });
        ```

### B. Driver App Implementation
**Goal**: Broadcast location continuously.

1.  **Auth**: Login as `DRIVER`.
2.  **Dashboard**: Fetch assigned schedule via `GET /api/driver/schedules/today`.
3.  **Background Tracking**:
    *   Use `expo-location`'s `watchPositionAsync`:
        ```javascript
        Location.watchPositionAsync({
          accuracy: Location.Accuracy.High,
          timeInterval: 5000, // Update every 5 seconds
          distanceInterval: 10, // Or every 10 meters
        }, (loc) => {
           // Send API call
           api.post('/driver/location', {
             scheduleId: currentScheduleId,
             latitude: loc.coords.latitude,
             longitude: loc.coords.longitude,
             speed: loc.coords.speed
           });
        });
        ```

### C. Admin App (can also be React Web)
**Goal**: Overview and Management.

1.  **Auth**: Login as `ADMIN`.
2.  **Bus Management**: List all buses using `GET /api/admin/buses`.
3.  **Map Overview**:
    *   Use a full-screen map.
    *   Subscribe to a global topic (if configured) or poll `GET` endpoints to show all active bus locations.

---

## 🏃 Running the Apps

To start an app:

```bash
npx expo start
```

*   Scan the QR code with **Expo Go** (Android) or Camera (iOS).
*   Ensure your phone has internet access to reach the Railway backend.

## ⚠️ Troubleshooting Common Issues

*   **Network Error / Connection Refused**:
    *   The Railway URL uses HTTPS/WSS. React Native handles this well, but ensure you aren't trying to connect to `http://localhost`. Localhost won't work on a physical device.
*   **WebSocket Disconnects**:
    *   Add heartbeat configurations in StompJS.
    *   Handle `reconnect` logic in your `socket.js` utility.
*   **Permission Denied (Location)**:
    *   Ensure you add permission requests in `app.json` or ask user for permission via `Location.requestForegroundPermissionsAsync()`.
