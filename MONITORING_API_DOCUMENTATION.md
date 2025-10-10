# Self-Healing System - Monitoring API Documentation

## Overview
This document provides comprehensive documentation for the monitoring and observability APIs in the Self-Healing System POC. These endpoints are designed to collect, analyze, and expose system metrics and error patterns for AI-driven analysis.

**Base URL:** `http://localhost:8080`

---

## 1. Monitoring APIs

### 1.1 Get Error Patterns
Retrieves detected error patterns and AI insights for analysis.

**Endpoint:** `GET /api/v1/monitoring/error-patterns`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/api/v1/monitoring/error-patterns \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "type": "VALIDATION_ERROR",
      "severity": "MEDIUM",
      "message": "Repeated error pattern detected: Invalid email format",
      "occurrenceCount": 7,
      "recommendation": "Review input validation rules and client-side validation"
    }
  ],
  "error": null,
  "timestamp": "2025-08-27T15:30:00"
}
```

### 1.2 Get Error Statistics
Retrieves summary statistics about error patterns.

**Endpoint:** `GET /api/v1/monitoring/error-statistics`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/api/v1/monitoring/error-statistics \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalPatterns": 5,
    "anomalousPatterns": 2,
    "errorsByType": {
      "VALIDATION_ERROR": 12,
      "USER_NOT_FOUND": 8,
      "FILE_OPERATION_ERROR": 3
    }
  },
  "error": null,
  "timestamp": "2025-08-27T15:30:00"
}
```

### 1.3 Get Health Metrics
Retrieves system health and performance metrics.

**Endpoint:** `GET /api/v1/monitoring/health-metrics`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/api/v1/monitoring/health-metrics \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "memory": {
      "total": 536870912,
      "free": 234567890,
      "used": 302303022,
      "max": 1073741824
    },
    "processors": 8,
    "timestamp": "2025-08-27T15:30:00",
    "customMetrics": {
      "note": "Custom metrics integration with Micrometer registry"
    }
  },
  "error": null,
  "timestamp": "2025-08-27T15:30:00"
}
```

### 1.4 Trigger Pattern Analysis
Manually triggers error pattern analysis.

**Endpoint:** `POST /api/v1/monitoring/analyze-patterns`

#### cURL Command:
```bash
curl -X POST http://localhost:8080/api/v1/monitoring/analyze-patterns \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": "Pattern analysis completed. Found 3 insights.",
  "error": null,
  "timestamp": "2025-08-27T15:30:00"
}
```

### 1.5 Record Test Error
Records a test error for pattern detection testing.

**Endpoint:** `POST /api/v1/monitoring/test-error`

#### cURL Command:
```bash
curl -X POST "http://localhost:8080/api/v1/monitoring/test-error" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "errorType=TEST_ERROR&errorMessage=This is a test error for pattern detection"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": "Test error recorded successfully",
  "error": null,
  "timestamp": "2025-08-27T15:30:00"
}
```

---

## 2. Enhanced Actuator Endpoints

### 2.1 Prometheus Metrics
Exposes metrics in Prometheus format for monitoring tools.

**Endpoint:** `GET /actuator/prometheus`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/actuator/prometheus
```

### 2.2 Detailed Health Check
Enhanced health check with detailed information.

**Endpoint:** `GET /actuator/health`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/actuator/health
```

**Expected Response (200 OK):**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### 2.3 Application Metrics
Detailed application metrics including custom metrics.

**Endpoint:** `GET /actuator/metrics`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/actuator/metrics
```

### 2.4 Specific Metric Details
Get details for a specific metric.

**Endpoint:** `GET /actuator/metrics/{metricName}`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/actuator/metrics/self_healing.requests.total
```

---

## 3. Correlation ID Support

All API requests now support correlation IDs for distributed tracing:

### 3.1 Request with Correlation ID
```bash
curl -X GET http://localhost:8080/api/v1/users \
  -H "X-Correlation-ID: my-trace-123" \
  -H "Accept: application/json"
```

### 3.2 Response with Correlation ID
The response will include the same correlation ID in the header:
```
X-Correlation-ID: my-trace-123
```

If no correlation ID is provided, the system will generate one automatically.

---

## 4. Error Pattern Detection

### 4.1 Automatic Error Recording
All errors are automatically recorded for pattern analysis:

- **Validation Errors**: Input validation failures
- **User Not Found**: Missing user lookup errors  
- **File Operation Errors**: File system related errors
- **Duplicate Email**: Email uniqueness violations
- **Internal Errors**: Unexpected system errors

### 4.2 Pattern Analysis Thresholds
- **Error Count Threshold**: 5 errors of the same type
- **Time Window**: 10 minutes
- **Error Rate Threshold**: 10% error rate

### 4.3 Severity Levels
- **LOW**: 5-9 occurrences
- **MEDIUM**: 10-14 occurrences  
- **HIGH**: 15-29 occurrences
- **CRITICAL**: 30+ occurrences

---

## 5. Custom Metrics

### 5.1 Available Custom Metrics
- `self_healing.requests.total` - Total HTTP requests
- `self_healing.errors.total` - Total errors
- `self_healing.validation_errors.total` - Validation errors
- `self_healing.file_operation_errors.total` - File operation errors
- `self_healing.request.duration` - Request processing time
- `self_healing.file_operation.duration` - File operation time
- `self_healing.error_by_type` - Errors grouped by type

### 5.2 Metric Tags
All metrics include these tags:
- `application=self-healing-system`
- `version=1.0.0`

---

## 6. Logging Enhancements

### 6.1 Structured JSON Logging
All logs are in structured JSON format with:
- Timestamp
- Log level
- Logger name
- Message
- MDC context (including correlation ID)
- Stack trace (for errors)

### 6.2 Log File Location
- **File**: `./logs/self-healing-app.log`
- **Rolling**: Daily rotation with size limits
- **Retention**: 30 days, max 100MB total

---

## 7. Integration with AI Analysis

### 7.1 Data Format for AI
Error patterns and metrics are structured for AI consumption:

```json
{
  "errorPattern": {
    "type": "VALIDATION_ERROR",
    "message": "Invalid email format",
    "occurrences": [
      {
        "timestamp": "2025-08-27T15:30:00",
        "correlationId": "abc-123",
        "stackTrace": "..."
      }
    ]
  },
  "context": {
    "systemMetrics": {
      "memoryUsage": 0.75,
      "cpuUsage": 0.45
    },
    "requestVolume": 150
  }
}
```

### 7.2 Recommended AI Analysis Workflow
1. **Collect**: Gather error patterns via `/api/v1/monitoring/error-patterns`
2. **Analyze**: Use AI to identify root causes and correlations
3. **Recommend**: Generate healing actions based on patterns
4. **Monitor**: Track effectiveness of applied solutions

---

## 8. Testing the Monitoring System

### 8.1 Generate Test Errors
```bash
# Create validation error
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name": "", "age": 200, "email": "invalid-email"}'

# Create user not found error  
curl -X GET http://localhost:8080/api/v1/users/nonexistent@example.com

# Record custom test error
curl -X POST "http://localhost:8080/api/v1/monitoring/test-error" \
  -d "errorType=CUSTOM_ERROR&errorMessage=Test error for AI analysis"
```

### 8.2 Analyze Patterns
```bash
# Get current patterns
curl -X GET http://localhost:8080/api/v1/monitoring/error-patterns

# Trigger analysis
curl -X POST http://localhost:8080/api/v1/monitoring/analyze-patterns

# Check statistics
curl -X GET http://localhost:8080/api/v1/monitoring/error-statistics
```

---

## 9. Troubleshooting

### 9.1 Common Issues
- **No metrics showing**: Check that Micrometer dependencies are included
- **Correlation ID missing**: Verify filter is registered correctly
- **Patterns not detected**: Ensure error threshold is reached (5+ errors)

### 9.2 Debug Endpoints
- Health: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Application info: `GET /actuator/info`

---

*Last Updated: August 27, 2025*
*Phase 2: Monitoring & Data Collection - Complete*