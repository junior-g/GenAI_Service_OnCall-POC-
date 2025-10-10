# Self-Healing User Management System

## Overview
A Spring Boot microservice that provides CRUD operations for user management with JSON file-based persistence. This service is designed as the foundation component for a self-healing system POC.

## Features
- RESTful API for user management (Create, Read, Update, Delete)
- JSON file-based persistence (no external database required)
- Thread-safe file operations with proper locking
- Comprehensive input validation
- Structured JSON logging for AI analysis
- Spring Boot Actuator integration for monitoring
- Global exception handling with consistent error responses

## Quick Start

### Prerequisites
- Java 21+
- Gradle 7.4+

### Running the Application
1. Clone the repository
2. Navigate to project directory
3. Run: `./gradlew bootRun`
4. The service will be available at: http://localhost:8080

### API Endpoints

#### Create User
```bash
POST /api/v1/users
Content-Type: application/json

{
  "name": "John Doe",
  "age": 30,
  "email": "john.doe@example.com"
}
```

#### Get User by Email
```bash
GET /api/v1/users/john.doe@example.com
```

#### Update User
```bash
PUT /api/v1/users/john.doe@example.com
Content-Type: application/json

{
  "name": "John Smith",
  "age": 31
}
```

#### Delete User
```bash
DELETE /api/v1/users/john.doe@example.com
```

#### Get All Users
```bash
GET /api/v1/users
```

### Data Storage
- Users are stored in: `./data/users.json`
- Logs are stored in: `./logs/self-healing-app.log`
- Thread-safe operations with file locking

### Monitoring Endpoints
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Logs: http://localhost:8080/actuator/logfile


## Architecture
This service follows the layered architecture pattern:
- **Controller Layer**: REST endpoints and request handling
- **Service Layer**: Business logic and validation
- **Repository Layer**: Data persistence with JSON files
- **Exception Handling**: Global exception handler for consistent error responses