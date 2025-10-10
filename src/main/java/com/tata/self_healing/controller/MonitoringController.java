package com.tata.self_healing.controller;

import com.tata.self_healing.dto.ApiResponse;
import com.tata.self_healing.monitoring.ErrorPatternDetector;
import com.tata.self_healing.monitoring.MetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for monitoring and metrics endpoints
 */
@RestController
@RequestMapping("/api/v1/monitoring")
@CrossOrigin(origins = "*")
public class MonitoringController {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);
    
    @Autowired
    private ErrorPatternDetector errorPatternDetector;
    
    @Autowired
    private MetricsCollector metricsCollector;
    
    /**
     * Get error patterns and insights for AI analysis
     */
    @GetMapping("/error-patterns")
    public ResponseEntity<ApiResponse<List<ErrorPatternDetector.ErrorInsight>>> getErrorPatterns() {
        logger.info("GET /api/v1/monitoring/error-patterns - Retrieving error patterns");
        
        try {
            List<ErrorPatternDetector.ErrorInsight> insights = errorPatternDetector.analyzePatterns();
            
            logger.info("Successfully retrieved {} error insights", insights.size());
            
            return ResponseEntity.ok(ApiResponse.success(insights));
        } catch (Exception e) {
            logger.error("Error retrieving error patterns", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("MONITORING_ERROR", "Failed to retrieve error patterns: " + e.getMessage()));
        }
    }
    
    /**
     * Get error statistics summary
     */
    @GetMapping("/error-statistics")
    public ResponseEntity<ApiResponse<ErrorPatternDetector.ErrorStatistics>> getErrorStatistics() {
        logger.info("GET /api/v1/monitoring/error-statistics - Retrieving error statistics");
        
        try {
            ErrorPatternDetector.ErrorStatistics stats = errorPatternDetector.getErrorStatistics();
            
            logger.info("Successfully retrieved error statistics");
            
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            logger.error("Error retrieving error statistics", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("MONITORING_ERROR", "Failed to retrieve error statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Get system health metrics
     */
    @GetMapping("/health-metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthMetrics() {
        logger.info("GET /api/v1/monitoring/health-metrics - Retrieving health metrics");
        
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // Add runtime metrics
            Runtime runtime = Runtime.getRuntime();
            metrics.put("memory", Map.of(
                    "total", runtime.totalMemory(),
                    "free", runtime.freeMemory(),
                    "used", runtime.totalMemory() - runtime.freeMemory(),
                    "max", runtime.maxMemory()
            ));
            
            metrics.put("processors", runtime.availableProcessors());
            metrics.put("timestamp", LocalDateTime.now());
            
            // Add custom metrics (these would come from MetricsCollector)
            metrics.put("customMetrics", Map.of(
                    "note", "Custom metrics integration with Micrometer registry"
            ));
            
            logger.info("Successfully retrieved health metrics");
            
            return ResponseEntity.ok(ApiResponse.success(metrics));
        } catch (Exception e) {
            logger.error("Error retrieving health metrics", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("MONITORING_ERROR", "Failed to retrieve health metrics: " + e.getMessage()));
        }
    }
    
    /**
     * Trigger manual error pattern analysis
     */
    @PostMapping("/analyze-patterns")
    public ResponseEntity<ApiResponse<String>> triggerPatternAnalysis() {
        logger.info("POST /api/v1/monitoring/analyze-patterns - Triggering manual pattern analysis");
        
        try {
            List<ErrorPatternDetector.ErrorInsight> insights = errorPatternDetector.analyzePatterns();
            
            String message = String.format("Pattern analysis completed. Found %d insights.", insights.size());
            logger.info(message);
            
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            logger.error("Error during pattern analysis", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("MONITORING_ERROR", "Failed to analyze patterns: " + e.getMessage()));
        }
    }
    
    /**
     * Record a test error for pattern detection testing
     */
    @PostMapping("/test-error")
    public ResponseEntity<ApiResponse<String>> recordTestError(
            @RequestParam String errorType,
            @RequestParam String errorMessage) {
        
        logger.info("POST /api/v1/monitoring/test-error - Recording test error: type={}, message={}", 
                errorType, errorMessage);
        
        try {
            errorPatternDetector.recordError(
                    errorType, 
                    errorMessage, 
                    "Test stack trace", 
                    "test-correlation-id"
            );
            
            String message = "Test error recorded successfully";
            logger.info(message);
            
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            logger.error("Error recording test error", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("MONITORING_ERROR", "Failed to record test error: " + e.getMessage()));
        }
    }
}