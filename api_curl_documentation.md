# Self Healing System - API Documentation

## Overview
This document provides all cURL commands and Postman configurations for testing the Self Healing System POC APIs.

**Base URL:** `http://localhost:8080`

---

## 1. User Management APIs

### 1.1 Create User
Creates a new user in the system.

**Endpoint:** `POST /api/v1/users`

#### cURL Command:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "age": 30,
    "email": "john.doe@example.com"
  }'
```

#### Postman Configuration:
- **Method:** POST
- **URL:** `http://localhost:8080/api/v1/users`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "name": "John Doe",
  "age": 30,
  "email": "john.doe@example.com"
}
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "name": "John Doe",
    "age": 30,
    "email": "john.doe@example.com",
    "createdAt": "2025-08-27T10:30:00",
    "updatedAt": "2025-08-27T10:30:00"
  },
  "error": null,
  "timestamp": "2025-08-27T10:30:00"
}
```

### 1.2 Get All Users
Retrieves all users from the system.

**Endpoint:** `GET /api/v1/users`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/api/v1/users \
  -H "Accept: application/json"
```

#### Postman Configuration:
- **Method:** GET
- **URL:** `http://localhost:8080/api/v1/users`
- **Headers:**
  - `Accept: application/json`

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "name": "John Doe",
      "age": 30,
      "email": "john.doe@example.com",
      "createdAt": "2025-08-27T10:30:00",
      "updatedAt": "2025-08-27T10:30:00"
    }
  ],
  "error": null,
  "timestamp": "2025-08-27T10:30:00"
}
```

### 1.3 Get User by Email
Retrieves a specific user by their email address.

**Endpoint:** `GET /api/v1/users/{email}`

#### cURL Command:
```bash
curl -X GET "http://localhost:8080/api/v1/users/john.doe@example.com" \
  -H "Accept: application/json"
```

#### Postman Configuration:
- **Method:** GET
- **URL:** `http://localhost:8080/api/v1/users/john.doe@example.com`
- **Headers:**
  - `Accept: application/json`

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "name": "John Doe",
    "age": 30,
    "email": "john.doe@example.com",
    "createdAt": "2025-08-27T10:30:00",
    "updatedAt": "2025-08-27T10:30:00"
  },
  "error": null,
  "timestamp": "2025-08-27T10:30:00"
}
```

### 1.4 Update User
Updates an existing user's information.

**Endpoint:** `PUT /api/v1/users/{email}`

#### cURL Command:
```bash
curl -X PUT "http://localhost:8080/api/v1/users/john.doe@example.com" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "age": 31,
    "email": "john.doe@example.com"
  }'
```

#### Postman Configuration:
- **Method:** PUT
- **URL:** `http://localhost:8080/api/v1/users/john.doe@example.com`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "name": "John Smith",
  "age": 31,
  "email": "john.doe@example.com"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "name": "John Smith",
    "age": 31,
    "email": "john.doe@example.com",
    "createdAt": "2025-08-27T10:30:00",
    "updatedAt": "2025-08-27T11:45:00"
  },
  "error": null,
  "timestamp": "2025-08-27T11:45:00"
}
```

### 1.5 Delete User
Deletes a user from the system.

**Endpoint:** `DELETE /api/v1/users/{email}`

#### cURL Command:
```bash
curl -X DELETE "http://localhost:8080/api/v1/users/john.doe@example.com" \
  -H "Accept: application/json"
```

#### Postman Configuration:
- **Method:** DELETE
- **URL:** `http://localhost:8080/api/v1/users/john.doe@example.com`
- **Headers:**
  - `Accept: application/json`

**Expected Response (204 No Content):**
```json
{
  "success": true,
  "data": null,
  "error": null,
  "timestamp": "2025-08-27T12:00:00"
}
```

---

## 2. Health Check APIs (Spring Boot Actuator)

### 2.1 Basic Health Check
Checks if the application is running.

**Endpoint:** `GET /actuator/health`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/actuator/health \
  -H "Accept: application/json"
```

#### Postman Configuration:
- **Method:** GET
- **URL:** `http://localhost:8080/actuator/health`
- **Headers:**
  - `Accept: application/json`

**Expected Response (200 OK):**
```json
{
  "status": "UP"
}
```

### 2.2 Application Info
Gets application information.

**Endpoint:** `GET /actuator/info`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/actuator/info \
  -H "Accept: application/json"
```

#### Postman Configuration:
- **Method:** GET
- **URL:** `http://localhost:8080/actuator/info`
- **Headers:**
  - `Accept: application/json`

---

## 3. Postman Collection Setup

### 3.1 Environment Variables
Create a Postman environment with these variables:
- `baseUrl`: `http://localhost:8080`
- `apiVersion`: `v1`

### 3.2 Collection Structure
```
Self Healing System API
├── User Management
│   ├── Create User
│   ├── Get All Users
│   ├── Get User by Email
│   ├── Update User
│   └── Delete User
└── Health Checks
    ├── Health Check
    └── App Info
```

### 3.3 Pre-request Scripts
Add this to collection pre-request scripts for dynamic timestamps:
```javascript
pm.environment.set("timestamp", new Date().toISOString());
```

---

## 4. Test Data Creation

### 4.1 Create Multiple Test Users for Postman

#### User 1:
```json
{
  "name": "Alice Johnson",
  "age": 25,
  "email": "alice.johnson@example.com"
}
```

#### User 2:
```json
{
  "name": "Bob Wilson",
  "age": 35,
  "email": "bob.wilson@example.com"
}
```

#### User 3:
```json
{
  "name": "Carol Brown",
  "age": 28,
  "email": "carol.brown@example.com"
}
```

---

## 5. Error Testing

### 5.1 Validation Errors

#### Invalid Email Format
**Request Body:**
```json
{
  "name": "Invalid User",
  "age": 25,
  "email": "invalid-email"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": {
      "email": "Invalid email format"
    }
  },
  "timestamp": "2025-08-27T10:30:00"
}
```

#### Missing Required Fields
**Request Body:**
```json
{
  "age": 25
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": {
      "name": "Name is required",
      "email": "Email is required"
    }
  },
  "timestamp": "2025-08-27T10:30:00"
}
```

#### Age Out of Range
**Request Body:**
```json
{
  "name": "Old User",
  "age": 200,
  "email": "old@example.com"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": {
      "age": "Age must be at most 150"
    }
  },
  "timestamp": "2025-08-27T10:30:00"
}
```

### 5.2 Not Found Errors

#### Get Non-existent User
**Expected Response (404 Not Found):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "User not found with email: nonexistent@example.com"
  },
  "timestamp": "2025-08-27T10:30:00"
}
```

#### Duplicate Email Error
**Expected Response (409 Conflict):**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "DUPLICATE_EMAIL",
    "message": "User already exists with email: john.doe@example.com"
  },
  "timestamp": "2025-08-27T10:30:00"
}
```

---

## 6. Postman Tests Scripts

### 6.1 Basic Response Validation
Add this to the Tests tab in Postman:

```javascript
pm.test("Status code is successful", function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 201, 204]);
});

pm.test("Response has success field", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('success');
});

pm.test("Response has timestamp", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('timestamp');
});
```

### 6.2 Create User Test
```javascript
pm.test("User created successfully", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
    pm.expect(jsonData.data).to.have.property('email');
    pm.expect(jsonData.data).to.have.property('createdAt');
    
    // Store email for subsequent requests
    pm.environment.set("userEmail", jsonData.data.email);
});
```

---

## 7. Response Codes Summary

| Endpoint | Method | Success Code | Error Codes |
|----------|--------|--------------|-------------|
| `/api/v1/users` | POST | 201 Created | 400 Bad Request, 409 Conflict |
| `/api/v1/users` | GET | 200 OK | - |
| `/api/v1/users/{email}` | GET | 200 OK | 404 Not Found |
| `/api/v1/users/{email}` | PUT | 200 OK | 400 Bad Request, 404 Not Found |
| `/api/v1/users/{email}` | DELETE | 204 No Content | 404 Not Found |
| `/actuator/health` | GET | 200 OK | - |
| `/actuator/info` | GET | 200 OK | - |

---

## 8. Common Headers

### Request Headers
```
Content-Type: application/json
Accept: application/json
```

### Response Headers
```
Content-Type: application/json
```

---

## 9. Data Storage Location

- **User Data:** `./data/users.json`
- **Application Logs:** `./logs/self-healing-app.log` (if configured)

---

## 10. Validation Rules

| Field | Rules |
|-------|-------|
| `name` | Required, 2-100 characters |
| `age` | Required, 1-150 |
| `email` | Required, valid email format, unique |

---

## Notes

1. **Email as ID**: The system uses email as the unique identifier for users.
2. **Timestamps**: All timestamps are in ISO 8601 format (`yyyy-MM-dd'T'HH:mm:ss`).
3. **API Response Format**: All responses follow the `ApiResponse<T>` wrapper format with `success`, `data`, `error`, and `timestamp` fields.
4. **Validation**: All input fields are validated according to Jakarta Bean Validation constraints.
5. **CORS**: The API supports cross-origin requests from any domain.
6. **File Storage**: User data is persisted in JSON files with thread-safe operations.

## Troubleshooting

### If you get 404 errors:
1. **Check Application Status**: Ensure the Spring Boot application is running with `./gradlew bootRun`
2. **Verify Dependencies**: Make sure you're using compatible Spring Boot versions (fixed in build.gradle)
3. **Check Logs**: Look for startup errors in the console output
4. **Test Health Endpoint**: Try `curl http://localhost:8080/actuator/health` first

### Common Issues Fixed:
- **Version Conflicts**: Updated to Spring Boot 3.3.2 with compatible dependencies
- **Missing Validation**: Added `spring-boot-starter-validation` dependency
- **Dependency Management**: Let Spring Boot manage all version compatibility

---

*Last Updated: August 27, 2025*