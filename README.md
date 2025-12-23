# 🚌 College Bus Tracker - Backend

A powerful Spring Boot backend for real-time college bus tracking, featuring REST APIs, WebSockets for live location updates, and secure role-based access.

---

## 🚀 Quick Start (Production/Server Deployment)

The easiest way to run this on any server is using **Docker and Docker Compose**.

### 1. Prerequisites
- [Docker](https://docs.docker.com/get-docker/) installed.
- [Docker Compose](https://docs.docker.com/compose/install/) installed.

### 2. Deployment Steps
```bash
# Clone the repository
git clone <repository-url>
cd bus-tracker

# Build the application (if running locally/JAR required for Docker)
./mvnw clean package -DskipTests

# Start the entire stack (Database, Redis, and Spring Boot App)
docker-compose up -d --build
```

The application will be available at: `http://<your-server-ip>:8080`

---

## 🛠️ Local Development Setup

If you want to run the application manually on your local machine:

### 1. Dependencies
- **Java 21**
- **Maven 3.x**
- **PostgreSQL** (Port: 5432, DB: `bus_tracker`, User: `postgres`, Pass: `123456`)
- **Redis** (Port: 6379)

### 2. Configuration
Edit `src/main/resources/application.yml` to update your local database credentials if they differ from the defaults.

### 3. Run the App
```bash
mvn spring-boot:run
```

---

## 📡 API Overview

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/login` | Public | Authenticate user and get token |
| GET | `/api/student/morning-buses` | Student | Get real-time bus locations & ETA |
| POST | `/api/driver/location` | Driver | Update live bus GPS coordinates |
| GET | `/api/admin/schedules` | Admin | Manage daily bus schedules |

---

## 🏗️ Folder Structure
- `com.bus_tracker.config`: Security (JWT) and WebSocket configuration.
- `com.bus_tracker.controller`: REST endpoints for Student, Driver, and Admin.
- `com.bus_tracker.entity`: Database models (User, Bus, Route, Stop, etc.).
- `com.bus_tracker.service`: Core business and tracking logic.
- `com.bus_tracker.websocket`: Real-time location broadcast handler.

---

## ✅ Health Check
To verify the server is running correctly, access:
`http://localhost:8080/actuator/health`

**Expected Response:**
```json
{ "status": "UP" }
```

---

## 🐳 Docker Stack Details
- **Postgres**: Persistence layer for all relational data.
- **Redis**: Fast, in-memory store for real-time location updates.
- **Spring Boot**: Main application service.

---

## 📜 License
This project is licensed under the MIT License.
