# � **College Bus Tracker - API Integration Guide**

This guide covers how to integrate the backend with **Student, Admin, and Driver** applications, specifically focusing on **React Native**.

---

## 🔌 **1. API Base URL Config**

**File**: `src/config/api.js`
```javascript
export const API_CONFIG = {
  BASE_URL: 'http://10.0.2.2:8080/api',  // Android Emulator
  // BASE_URL: 'http://192.168.1.100:8080/api',  // Real device (your laptop IP)
  WS_URL: 'ws://10.0.2.2:8080/ws'        // WebSocket
};
```

---

## � **2. STUDENT APP - Complete Flow**

### **A. Login Screen**
```javascript
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const login = async (email, password) => {
  try {
    const response = await axios.post(`${API_CONFIG.BASE_URL}/auth/login`, {
      email, password
    });
    await AsyncStorage.setItem('token', response.data.accessToken);
    await AsyncStorage.setItem('role', response.data.role);
    navigation.navigate('Home');
  } catch (error) {
    Alert.alert('Login failed');
  }
};
```

### **B. Morning Tracking Screen**
```javascript
// Get nearby stops
const getNearbyStops = async (lat, lng) => {
  const token = await AsyncStorage.getItem('token');
  const { data } = await axios.get(`${API_CONFIG.BASE_URL}/student/stops/nearby`, {
    params: { lat, lng, radius: 1000 },
    headers: { Authorization: `Bearer ${token}` }
  });
  return data;
};

// Get morning buses
const getMorningBuses = async (date, stopId) => {
  const token = await AsyncStorage.getItem('token');
  const { data } = await axios.get(`${API_CONFIG.BASE_URL}/student/morning-buses`, {
    params: { date: date.toISOString().split('T')[0], stopId },
    headers: { Authorization: `Bearer ${token}` }
  });
  return data;
};
```

### **C. Live Tracking Map (WebSocket)**
```javascript
import io from 'socket.io-client';

useEffect(() => {
  const socket = io(API_CONFIG.WS_URL);
  socket.on('connect', () => {
    socket.emit('subscribe', 'topic/bus/1');
  });
  
  socket.on('topic/bus/1', (location) => {
    setBusLocation(location);  // Update map marker
  });
  
  return () => socket.disconnect();
}, []);
```

---

## �‍💼 **3. ADMIN APP - Management**

### **Bus & Schedule Management**
```javascript
const createBus = async (busData) => {
  const token = await AsyncStorage.getItem('token');
  await axios.post(`${API_CONFIG.BASE_URL}/admin/buses`, busData, {
    headers: { Authorization: `Bearer ${token}` }
  });
};

const updateSchedule = async (scheduleId, updateData) => {
  const token = await AsyncStorage.getItem('token');
  await axios.put(`${API_CONFIG.BASE_URL}/admin/schedules/${scheduleId}`, updateData, {
    headers: { Authorization: `Bearer ${token}` }
  });
};
```

---

## 🚛 **4. DRIVER APP - GPS Tracking**

### **Send GPS Updates**
```javascript
const sendLocation = async (coords) => {
  const token = await AsyncStorage.getItem('token');
  await axios.post(`${API_CONFIG.BASE_URL}/driver/location`, {
    scheduleId: currentScheduleId,
    latitude: coords.latitude,
    longitude: coords.longitude,
    speed: coords.speed
  }, {
    headers: { Authorization: `Bearer ${token}` }
  });
};
```

---

## 📋 **5. Success Checklist**
```
✅ Backend: http://localhost:8080/actuator/health = UP
✅ Student: Can see nearby stops + morning buses
✅ Admin: Can manage buses + schedules
✅ Driver: Sending GPS updates successfully
✅ Map: WebSocket markers moving in real-time
```
