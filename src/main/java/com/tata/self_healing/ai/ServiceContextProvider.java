package com.tata.self_healing.ai;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides comprehensive service context and business knowledge for LLM training
 */
@Component
public class ServiceContextProvider {
    
    /**
     * Get comprehensive service context for LLM training
     */
    public String getServiceContext() {
        return """
            # Self-Healing User Management System - Service Context
            
            ## System Overview
            You are an AI assistant specialized in analyzing and healing a Spring Boot User Management microservice.
            This service is part of a larger self-healing system POC designed to automatically detect, analyze, and resolve issues.
            
            ## Business Domain: User Management
            - **Primary Function**: CRUD operations for user profiles
            - **Data Model**: Users have name (2-100 chars), age (1-150), email (unique identifier)
            - **Storage**: JSON file-based persistence (./data/users.json)
            - **Technology**: Spring Boot 3.x, Java 21, JSON file operations
            
            ## Architecture Components
            1. **Controller Layer**: REST endpoints (/api/v1/users/*)
            2. **Service Layer**: Business logic and validation (UserService)
            3. **Repository Layer**: JSON file operations (JsonFileUserRepository)
            4. **Exception Handling**: Global exception handler with structured responses
            5. **Monitoring Layer**: Error pattern detection and metrics collection
            
            ## Common Error Patterns & Business Context
            
            ### VALIDATION_ERROR
            - **Cause**: Invalid user input (empty name, invalid email, age out of range)
            - **Business Impact**: Prevents data corruption, maintains data quality
            - **Common Triggers**: Frontend validation bypass, API misuse, malformed requests
            - **Healing Strategy**: Enhance validation, improve error messages, client-side validation
            
            ### USER_NOT_FOUND
            - **Cause**: Lookup for non-existent email address
            - **Business Impact**: Poor user experience, potential data inconsistency
            - **Common Triggers**: Deleted users, typos in email, race conditions
            - **Healing Strategy**: Implement soft deletes, suggestion system, data verification
            
            ### DUPLICATE_EMAIL
            - **Cause**: Attempt to create user with existing email
            - **Business Impact**: Data integrity violation, user confusion
            - **Common Triggers**: Concurrent requests, poor UI feedback, data migration issues
            - **Healing Strategy**: Better duplicate checking, user feedback, conflict resolution
            
            ### FILE_OPERATION_ERROR
            - **Cause**: JSON file read/write failures
            - **Business Impact**: Data loss risk, service unavailability
            - **Common Triggers**: Disk space, permissions, file corruption, concurrent access
            - **Healing Strategy**: Disk monitoring, backup systems, file locking, retry mechanisms
            
            ### INTERNAL_ERROR
            - **Cause**: Unexpected system errors
            - **Business Impact**: Service degradation, user frustration
            - **Common Triggers**: Memory issues, dependency failures, configuration problems
            - **Healing Strategy**: Resource monitoring, graceful degradation, circuit breakers
            
            ## System Constraints & Characteristics
            - **Concurrency**: Thread-safe file operations with NIO locking
            - **Performance**: Target <200ms response time, support 10 concurrent users
            - **Data Volume**: Optimized for up to 1000 users (POC scale)
            - **Reliability**: Atomic file operations, corruption recovery
            - **Monitoring**: Structured JSON logging, correlation ID tracking
            
            ## Healing Priorities
            1. **Data Integrity**: Prevent data corruption at all costs
            2. **Service Availability**: Maintain service uptime
            3. **User Experience**: Minimize user-facing errors
            4. **Performance**: Maintain response time targets
            5. **Observability**: Ensure proper logging and monitoring
            
            ## Integration Context
            - **Environment**: Local development (macOS), single instance
            - **Dependencies**: No external databases, minimal external services
            - **Deployment**: Standalone JAR, embedded Tomcat
            - **Monitoring**: Micrometer metrics, Prometheus export, custom dashboards
            
            ## AI Analysis Guidelines
            When analyzing errors and recommending healing actions:
            1. Consider the business impact and user experience
            2. Prioritize data integrity and service availability
            3. Suggest both immediate fixes and long-term improvements
            4. Account for the file-based storage limitations
            5. Consider the POC nature but recommend production-ready solutions
            6. Focus on automated healing where possible
            7. Provide specific, actionable recommendations
            
            This context should guide your analysis and recommendations for optimal system healing.
            """;
    }
    
    /**
     * Get error-specific context for detailed analysis
     */
    public Map<String, String> getErrorSpecificContext() {
        Map<String, String> context = new HashMap<>();
        
        context.put("VALIDATION_ERROR", """
            Validation errors indicate client-side issues or API misuse.
            Focus on: Input sanitization, client validation, API documentation, error messaging.
            Business Impact: Medium - prevents bad data but affects user experience.
            Auto-healing: Enhance validation rules, improve error responses.
            """);
            
        context.put("USER_NOT_FOUND", """
            User lookup failures may indicate data consistency issues or user errors.
            Focus on: Data integrity, user guidance, search functionality, soft deletes.
            Business Impact: Medium - affects user experience and data reliability.
            Auto-healing: Implement user suggestions, verify data consistency.
            """);
            
        context.put("DUPLICATE_EMAIL", """
            Email uniqueness violations indicate race conditions or poor user feedback.
            Focus on: Concurrency control, user feedback, duplicate detection.
            Business Impact: High - violates core business rule of unique emails.
            Auto-healing: Implement proper locking, improve duplicate checking.
            """);
            
        context.put("FILE_OPERATION_ERROR", """
            File system errors are critical and can cause data loss.
            Focus on: Disk space, permissions, backup systems, retry mechanisms.
            Business Impact: Critical - can cause data loss and service outage.
            Auto-healing: Monitor disk space, implement backups, add retry logic.
            """);
            
        context.put("INTERNAL_ERROR", """
            System-level errors indicate infrastructure or code issues.
            Focus on: Resource monitoring, error handling, graceful degradation.
            Business Impact: High - indicates system instability.
            Auto-healing: Resource monitoring, circuit breakers, graceful fallbacks.
            """);
            
        return context;
    }
    
    /**
     * Get healing action templates based on error patterns
     */
    public Map<String, String> getHealingActionTemplates() {
        Map<String, String> templates = new HashMap<>();
        
        templates.put("DISK_SPACE_LOW", """
            {
                "action": "CLEANUP_TEMP_FILES",
                "priority": "HIGH",
                "automated": true,
                "steps": [
                    "Check available disk space",
                    "Clean temporary files older than 7 days",
                    "Rotate log files if needed",
                    "Alert if space still low"
                ]
            }
            """);
            
        templates.put("HIGH_ERROR_RATE", """
            {
                "action": "CIRCUIT_BREAKER_ACTIVATION",
                "priority": "CRITICAL", 
                "automated": true,
                "steps": [
                    "Activate circuit breaker for failing operations",
                    "Return cached responses where possible",
                    "Alert operations team",
                    "Monitor for recovery"
                ]
            }
            """);
            
        templates.put("MEMORY_PRESSURE", """
            {
                "action": "MEMORY_OPTIMIZATION",
                "priority": "HIGH",
                "automated": true,
                "steps": [
                    "Force garbage collection",
                    "Clear non-essential caches",
                    "Reduce thread pool sizes temporarily",
                    "Monitor memory usage trends"
                ]
            }
            """);
            
        return templates;
    }
}