# Academic Progress Monitoring Platform

A distributed microservices application for managing student attendance, assignments, and academic progress. Built with Spring Boot, gRPC, RabbitMQ, Docker, and Kubernetes.

---

## Architecture Overview

```
Browser/Client
      ↓
BFF (Port 8080) — JWT validation, request proxying
      ↓
┌─────────────────────────────────────────────────┐
│                Internal Services                 │
├─────────────────┬───────────────────────────────┤
│ Auth Service    │ Student Service (+ gRPC)       │
│ Port 8081       │ Port 8082 / gRPC 9090          │
├─────────────────┼───────────────────────────────┤
│ Attendance      │ Assignment Service             │
│ Service 8083    │ Port 8084                      │
├─────────────────┴───────────────────────────────┤
│ Notification Service — Port 8085                 │
└─────────────────────────────────────────────────┘
      ↓
RabbitMQ (Event Bus)
```

### Services

| Service | Port | Description |
|---|---|---|
| BFF | 8080 | Backend-for-Frontend, JWT validation, proxying |
| Auth Service | 8081 | User registration, login, JWT generation |
| Student Service | 8082 | Student profiles, gRPC endpoint |
| Attendance Service | 8083 | Attendance recording, 3-absence warning bot |
| Assignment Service | 8084 | Assignment management, grading |
| Notification Service | 8085 | Email notifications via RabbitMQ events |

### Databases

| Database | Port | Service |
|---|---|---|
| authdb | 5432 | Auth Service |
| studentdb | 5433 | Student Service |
| attendancedb | 5434 | Attendance Service |
| assignmentdb | 5435 | Assignment Service |
| notificationdb | 5436 | Notification Service |

---

## Tech Stack

- **Backend:** Java 24, Spring Boot 4.0.6
- **Communication:** REST API, gRPC 1.77.1, RabbitMQ
- **Authentication:** JWT (jjwt 0.12.6)
- **Database:** PostgreSQL 16
- **Frontend:** Thymeleaf (server-side templates)
- **Containerization:** Docker
- **Orchestration:** Kubernetes (Minikube)
- **Email:** Mailtrap (development SMTP)

---

## Features

### Teacher
- Register and manage students
- Record attendance (Present, Late, Absent, Sick)
- Create assignments for all students at once
- Grade assignments with feedback categories
- Dashboard with overview stats and pending submissions

### Student
- View profile and attendance statistics
- See active and completed assignments
- Submit assignments with comments
- View grades and teacher feedback
- Receive email notifications for new assignments and grades

### Automated (Event-Driven)
- Attendance warning fires automatically after 3 absences
- Email reminders sent when assignments are created
- Email notifications sent when assignments are graded

---

## Event Flow

```
Assignment created → assignment.exchange → assignment-created queue
                                        → Notification Service → Email

Student submits  → assignment.exchange → assignment-graded queue
                                       → Notification Service → Email

3rd absence recorded → attendance.exchange → attendance-warning queue
                                           → Notification Service → Email
```

---

## Running Locally

### Prerequisites
- Docker Desktop
- Java 24
- Maven 3.9+

### Start all services with Docker Compose

```bash
# Build all jars
cd auth-service && mvn clean package -DskipTests && cd ..
cd studentservice && mvn clean package -DskipTests && cd ..
cd attendanceservice && mvn clean package -DskipTests && cd ..
cd assignmentservice && mvn clean package -DskipTests && cd ..
cd notificationservice && mvn clean package -DskipTests && cd ..
cd bff && mvn clean package -DskipTests && cd ..

# Build Docker images
docker compose build

# Start everything
docker compose up -d

# Check all containers are running
docker compose ps
```

Open browser at: `http://localhost:8080`

### Stop everything

```bash
docker compose down
```

---

## Running in Kubernetes (VG)

### Prerequisites
- Minikube
- kubectl

### Start

```bash
# Start Minikube
minikube start --driver=docker --memory=4096 --cpus=2

# Load Docker images into Minikube
minikube image load academic-platform-authservice:latest
minikube image load academic-platform-studentservice:latest
minikube image load academic-platform-attendanceservice:latest
minikube image load academic-platform-assignmentservice:latest
minikube image load academic-platform-notificationservice:latest
minikube image load academic-platform-bff:latest

# Deploy all services
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/databases/
kubectl apply -f k8s/rabbitmq/
kubectl apply -f k8s/services/

# Verify all pods are running
kubectl get pods -n academic

# Get the application URL
minikube service bff -n academic --url
```

### Internal DNS Communication

Services communicate via Kubernetes internal DNS — no IP addresses:

```
authservice    → jdbc:postgresql://authdb:5432/authdb
studentservice → jdbc:postgresql://studentdb:5432/studentdb
bff            → http://auth-service:8081
bff            → http://student-service:8082
attendanceservice → rabbitmq:5672
```

### Update images in Kubernetes

```bash
# Tag new version
docker tag academic-platform-bff:latest academic-platform-bff:v2
minikube image load academic-platform-bff:v2

# Update yaml image tag and apply
kubectl apply -f k8s/services/bff.yaml

# Restart all deployments
kubectl rollout restart deployment -n academic
kubectl get pods -n academic --watch
```

---

## API Endpoints

All requests go through BFF on port 8080. JWT token required in `Authorization: Bearer <token>` header for all endpoints except auth.

### Auth
```
POST /api/auth/register   — Register new user
POST /api/auth/login      — Login, returns JWT token
```

### Students
```
POST   /api/students                        — Create student profile
GET    /api/students/{id}                   — Get student
GET    /api/students/teacher/{teacherId}    — Get teacher's students
PUT    /api/students/{id}                   — Update student
DELETE /api/students/{id}                   — Delete student
```

### Attendance
```
POST /api/attendance                              — Record attendance
GET  /api/attendance/student/{id}                 — Get student attendance
GET  /api/attendance/student/{id}/stats           — Get attendance statistics
```

### Assignments
```
POST /api/assignments           — Create assignment (single student)
POST /api/assignments/all       — Create assignment for all students
GET  /api/assignments/{id}      — Get assignment
GET  /api/assignments/student/{studentId}  — Get student assignments
GET  /api/assignments/teacher/{teacherId}  — Get teacher assignments
PUT  /api/assignments/{id}/grade   — Grade assignment
PUT  /api/assignments/{id}/submit  — Submit assignment
```

### Notifications
```
GET /api/notifications/student/{id}   — Get student notifications
GET /api/notifications/recent         — Get recent notifications
```

---

## Project Structure

```
academic-platform/
├── auth-service/           — JWT auth, user registration
├── studentservice/         — Student profiles, gRPC server
├── attendanceservice/      — Attendance tracking, warning events
├── assignmentservice/      — Assignments, grading, events
├── notificationservice/    — Email notifications, event consumer
├── bff/                    — API gateway, JWT validation, Thymeleaf UI
│   └── src/main/resources/
│       └── templates/      — HTML pages (Thymeleaf)
├── proto/                  — Shared gRPC contract definitions
├── k8s/                    — Kubernetes manifests
│   ├── namespace.yaml
│   ├── databases/
│   ├── rabbitmq/
│   └── services/
└── docker-compose.yml      — Local development setup
```

---

## Security

- JWT tokens validated on every request at BFF level
- Passwords hashed with BCrypt
- Internal services not exposed outside Docker/Kubernetes network
- Services communicate via internal DNS only
- Role-based access: TEACHER and STUDENT roles

