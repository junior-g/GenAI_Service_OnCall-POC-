# Self-Healing System - AI API Documentation

## Overview
This document provides comprehensive documentation for the AI-powered self-healing capabilities in Phase 3. The system uses GenAI for intelligent error analysis and automated healing recommendations with full service context awareness.

**Base URL:** `http://localhost:8080`

---

## 🤖 AI-Powered Self-Healing APIs

### 1.1 Trigger AI Analysis
Performs comprehensive AI analysis of current error patterns with full service context.

**Endpoint:** `POST /api/v1/ai/analyze`

#### cURL Command:
```bash
curl -X POST http://localhost:8080/api/v1/ai/analyze \
  -H "Content-Type: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "analysisId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "timestamp": "2025-08-27T16:30:00",
    "rootCauseAnalysis": "Analysis indicates a cascade failure pattern. High validation error rates suggest either client-side validation bypass or API misuse...",
    "businessImpact": "HIGH - Error rates above threshold indicate degraded user experience. User registration and profile management operations are likely failing...",
    "correlations": [
      "Validation errors correlate with client-side integration issues",
      "High error rate correlates with increased system load"
    ],
    "recommendations": [
      {
        "recommendationId": "rec-001",
        "action": "Enhance input validation and error messaging",
        "priority": "HIGH",
        "automated": true,
        "implementation": "Add client-side validation, improve API error responses with field-specific messages, implement request sanitization",
        "expectedOutcome": "Reduce validation errors by 70%, improve user experience"
      }
    ],
    "preventionStrategies": [
      "Implement comprehensive input validation at API gateway level",
      "Add automated testing for concurrent file operations"
    ],
    "automationOpportunities": [
      "Automated disk cleanup and log rotation",
      "Self-healing file corruption detection and recovery"
    ],
    "confidence": 0.92
  },
  "timestamp": "2025-08-27T16:30:00"
}
```

### 1.2 Execute Automated Healing
Triggers AI analysis and executes automated healing actions based on recommendations.

**Endpoint:** `POST /api/v1/ai/heal`

#### cURL Command:
```bash
curl -X POST http://localhost:8080/api/v1/ai/heal \
  -H "Content-Type: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "executionId": "exec-001",
      "recommendationId": "rec-001",
      "action": "Enhance input validation and error messaging",
      "status": "SUCCESS",
      "message": "Validation enhancement applied",
      "details": "Enhanced input validation rules and error messaging",
      "startTime": "2025-08-27T16:30:05",
      "endTime": "2025-08-27T16:30:07",
      "durationMs": 2150
    }
  ],
  "timestamp": "2025-08-27T16:30:07"
}
```

### 1.3 Get Healing History
Retrieves the history of executed healing actions.

**Endpoint:** `GET /api/v1/ai/healing-history`

#### cURL Command:
```bash
curl -X GET "http://localhost:8080/api/v1/ai/healing-history?limit=10" \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "executionId": "exec-001",
      "recommendation": {
        "recommendationId": "rec-001",
        "action": "Enhance input validation and error messaging",
        "priority": "HIGH",
        "automated": true
      },
      "result": {
        "status": "SUCCESS",
        "message": "Validation enhancement applied",
        "durationMs": 2150
      },
      "timestamp": "2025-08-27T16:30:07"
    }
  ],
  "timestamp": "2025-08-27T16:30:15"
}
```

### 1.4 Get AI System Statistics
Retrieves comprehensive statistics about the AI system performance.

**Endpoint:** `GET /api/v1/ai/statistics`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/api/v1/ai/statistics \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "healingEngine": {
      "totalExecutions": 15,
      "successfulExecutions": 13,
      "successRate": 0.87,
      "automationEnabled": true,
      "dryRunMode": false
    },
    "errorPatterns": {
      "totalPatterns": 7,
      "anomalousPatterns": 2,
      "errorsByType": {
        "VALIDATION_ERROR": 5,
        "FILE_OPERATION_ERROR": 2
      }
    },
    "systemHealth": {
      "memoryUsage": 0.45,
      "availableProcessors": 12
    },
    "timestamp": "2025-08-27T16:30:20"
  },
  "timestamp": "2025-08-27T16:30:20"
}
```

### 1.5 Test AI Analysis
Tests AI analysis with custom error scenarios for validation.

**Endpoint:** `POST /api/v1/ai/test-analysis`

#### cURL Command:
```bash
curl -X POST "http://localhost:8080/api/v1/ai/test-analysis" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "errorType=VALIDATION_ERROR&errorMessage=Invalid email format detected&severity=HIGH"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "analysisId": "test-analysis-001",
    "rootCauseAnalysis": "Test analysis for VALIDATION_ERROR pattern...",
    "businessImpact": "HIGH - Validation errors indicate client-side issues...",
    "recommendations": [
      {
        "action": "Enhance input validation and error messaging",
        "priority": "HIGH",
        "automated": true
      }
    ],
    "confidence": 0.88
  },
  "timestamp": "2025-08-27T16:30:25"
}
```

### 1.6 AI System Health Check
Checks the health and configuration of the AI system.

**Endpoint:** `GET /api/v1/ai/health`

#### cURL Command:
```bash
curl -X GET http://localhost:8080/api/v1/ai/health \
  -H "Accept: application/json"
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "aiAnalysisService": "OPERATIONAL",
    "healingEngine": "OPERATIONAL",
    "errorPatternDetector": "OPERATIONAL",
    "configuration": {
      "aiEnabled": true,
      "mockMode": true,
      "automationEnabled": true,
      "dryRunMode": false
    },
    "recentActivity": {
      "lastAnalysis": "Available on demand",
      "lastHealing": "Available on demand",
      "totalPatterns": 7
    },
    "status": "HEALTHY",
    "timestamp": "2025-08-27T16:30:30"
  },
  "timestamp": "2025-08-27T16:30:30"
}
```

---

## 🧠 Service Context Training

### AI Knowledge Base
The AI system is trained with comprehensive service context including:

#### Business Domain Knowledge
- **User Management System**: CRUD operations for user profiles
- **Data Model**: Users with name, age, email constraints
- **Storage Architecture**: JSON file-based persistence
- **Performance Targets**: <200ms response time, 10 concurrent users
- **Data Volume**: Optimized for 1000 users (POC scale)

#### Error Pattern Expertise
- **VALIDATION_ERROR**: Client-side issues, input validation failures
- **USER_NOT_FOUND**: Data consistency, user experience issues
- **DUPLICATE_EMAIL**: Concurrency problems, business rule violations
- **FILE_OPERATION_ERROR**: Infrastructure issues, data integrity risks
- **INTERNAL_ERROR**: System-level problems, stability concerns

#### Healing Strategies
- **Data Integrity Priority**: Prevent corruption at all costs
- **Service Availability**: Maintain uptime and responsiveness
- **User Experience**: Minimize user-facing errors
- **Automated Recovery**: Self-healing where possible
- **Proactive Prevention**: Long-term system improvements

---

## 🔧 Automated Healing Actions

### Available Healing Actions

#### 1. Disk Cleanup
- **Trigger**: Disk space issues, file operation errors
- **Actions**: Clean temp files, rotate logs, free disk space
- **Automation**: Fully automated
- **Safety**: Limited scope, non-destructive

#### 2. File Backup
- **Trigger**: File operation errors, data integrity risks
- **Actions**: Create backup of users.json, ensure data safety
- **Automation**: Fully automated
- **Safety**: Read-only operation, creates backups

#### 3. Validation Enhancement
- **Trigger**: High validation error rates
- **Actions**: Update validation rules, improve error messages
- **Automation**: Configuration-based
- **Safety**: Non-breaking changes

#### 4. Monitoring Enhancement
- **Trigger**: Pattern detection needs
- **Actions**: Adjust thresholds, enhance alerting
- **Automation**: Configuration updates
- **Safety**: Observability improvements only

### Execution Modes

#### Production Mode (`dry-run=false`)
- Executes actual healing actions
- Makes real system changes
- Requires careful monitoring
- Full automation capabilities

#### Dry Run Mode (`dry-run=true`)
- Simulates healing actions
- No actual system changes
- Safe for testing and validation
- Full analysis and recommendations

---

## 📊 AI Analysis Process

### 1. Context Preparation
```
Service Context + Error Patterns + System State
↓
Comprehensive Analysis Prompt
```

### 2. AI Analysis
```
LLM Processing with Service Knowledge
↓
Root Cause Analysis + Business Impact + Recommendations
```

### 3. Recommendation Execution
```
Automated Actions + Manual Actions + Monitoring
↓
Execution Results + Learning Feedback
```

### 4. Continuous Learning
```
Execution Outcomes + Pattern Updates + Knowledge Refinement
↓
Improved Future Recommendations
```

---

## 🎯 Business Impact Assessment

### Impact Levels

#### CRITICAL
- Data loss risk
- Service unavailability
- Security vulnerabilities
- **Action**: Immediate automated healing

#### HIGH
- Degraded user experience
- Performance issues
- Business rule violations
- **Action**: Prioritized automated healing

#### MEDIUM
- Minor user inconvenience
- Operational inefficiencies
- Monitoring gaps
- **Action**: Scheduled healing or manual intervention

#### LOW
- Cosmetic issues
- Non-critical optimizations
- Future improvements
- **Action**: Backlog for future releases

---

## 🔄 Integration Workflow

### Complete Self-Healing Cycle

1. **Error Detection** (Phase 2)
   - Monitor error patterns
   - Detect anomalies
   - Collect system context

2. **AI Analysis** (Phase 3)
   - Analyze with service context
   - Generate recommendations
   - Assess business impact

3. **Automated Healing** (Phase 3)
   - Execute safe actions
   - Monitor results
   - Update knowledge base

4. **Validation & Learning** (Phase 3)
   - Verify healing effectiveness
   - Update patterns
   - Improve recommendations

---

## 🧪 Testing Scenarios

### Scenario 1: High Validation Error Rate
```bash
# Generate validation errors
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name": "", "age": 200, "email": "invalid"}'

# Trigger AI analysis
curl -X POST http://localhost:8080/api/v1/ai/analyze

# Execute healing
curl -X POST http://localhost:8080/api/v1/ai/heal
```

### Scenario 2: File Operation Issues
```bash
# Simulate file errors (via test endpoint)
curl -X POST http://localhost:8080/api/v1/monitoring/test-error \
  -d "errorType=FILE_OPERATION_ERROR&errorMessage=Disk space low"

# Analyze and heal
curl -X POST http://localhost:8080/api/v1/ai/heal
```

### Scenario 3: Custom Error Analysis
```bash
# Test specific error pattern
curl -X POST http://localhost:8080/api/v1/ai/test-analysis \
  -d "errorType=CUSTOM_ERROR&errorMessage=Custom test scenario&severity=CRITICAL"
```

---

## 📈 Performance Metrics

### AI Analysis Performance
- **Analysis Time**: 500-2000ms (mock mode)
- **Confidence Scores**: 0.85-0.95 typical range
- **Recommendation Accuracy**: Based on service context training
- **Automation Rate**: 70-80% of recommendations can be automated

### Healing Execution Performance
- **Execution Time**: 100-5000ms depending on action
- **Success Rate**: 85-95% for automated actions
- **Safety**: Dry-run mode available for testing
- **Rollback**: Manual rollback for critical actions

---

## 🔒 Safety & Security

### Safety Measures
- **Dry Run Mode**: Test without system changes
- **Limited Scope**: Healing actions have defined boundaries
- **Monitoring**: All actions are logged and monitored
- **Rollback**: Manual intervention capabilities

### Security Considerations
- **Access Control**: API endpoints require appropriate access
- **Audit Trail**: All AI decisions and actions are logged
- **Data Privacy**: No sensitive data in AI analysis
- **Validation**: All healing actions are validated before execution

---

## 🚀 Future Enhancements

### Real LLM Integration
- OpenAI GPT-4 integration
- Azure OpenAI Service
- Custom model fine-tuning
- Real-time learning capabilities

### Advanced Healing Actions
- Database migration automation
- Infrastructure scaling
- Security patch automation
- Performance optimization

### Enhanced Learning
- Feedback loop implementation
- Pattern recognition improvement
- Predictive analysis
- Proactive healing

---

*Last Updated: August 27, 2025*
*Phase 3: GenAI Integration - Complete*