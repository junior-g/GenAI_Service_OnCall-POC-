package com.tata.self_healing.controller;

import com.tata.self_healing.ai.AutomatedHealingEngine;
import com.tata.self_healing.ai.GenAIAnalysisService;
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
import java.util.concurrent.CompletableFuture;

/**
 * Controller for AI-powered self-healing capabilities
 */
@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*")
public class AIController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);
    
    @Autowired
    private GenAIAnalysisService genAIAnalysisService;
    
    @Autowired
    private AutomatedHealingEngine healingEngine;
    
    @Autowired
    private ErrorPatternDetector errorPatternDetector;
    
    @Autowired
    private MetricsCollector metricsCollector;
    
    /**
     * Trigger comprehensive AI analysis of current error patterns
     */
    @PostMapping("/analyze")
    public CompletableFuture<ResponseEntity<ApiResponse<GenAIAnalysisService.AIAnalysisResult>>> 
            triggerAIAnalysis() {
        
        logger.info("POST /api/v1/ai/analyze - Triggering AI analysis");
        
        try {
            // Get current error patterns
            List<ErrorPatternDetector.ErrorInsight> insights = errorPatternDetector.analyzePatterns();
            
            // Prepare system context
            Map<String, Object> systemContext = buildSystemContext();
            
            // Trigger AI analysis
            return genAIAnalysisService.analyzeErrorPatterns(insights, systemContext)
                    .thenApply(result -> {
                        logger.info("AI analysis completed with confidence: {}", result.getConfidence());
                        return ResponseEntity.ok(ApiResponse.success(result));
                    })
                    .exceptionally(throwable -> {
                        logger.error("Error during AI analysis", throwable);
                        return ResponseEntity.internalServerError().body(
                                ApiResponse.error("AI_ANALYSIS_ERROR", 
                                        "Failed to complete AI analysis: " + throwable.getMessage()));
                    });
            
        } catch (Exception e) {
            logger.error("Error triggering AI analysis", e);
            return CompletableFuture.completedFuture(
                    ResponseEntity.internalServerError().body(
                            ApiResponse.error("AI_ANALYSIS_ERROR", 
                                    "Failed to trigger AI analysis: " + e.getMessage())));
        }
    }
    
    /**
     * Execute automated healing based on AI recommendations
     */
    @PostMapping("/heal")
    public CompletableFuture<ResponseEntity<ApiResponse<List<AutomatedHealingEngine.HealingExecutionResult>>>> 
            executeAutomatedHealing() {
        
        logger.info("POST /api/v1/ai/heal - Executing automated healing");
        
        try {
            // First get AI analysis
            List<ErrorPatternDetector.ErrorInsight> insights = errorPatternDetector.analyzePatterns();
            Map<String, Object> systemContext = buildSystemContext();
            
            return genAIAnalysisService.analyzeErrorPatterns(insights, systemContext)
                    .thenCompose(analysisResult -> {
                        logger.info("AI analysis completed, executing {} recommendations", 
                                analysisResult.getRecommendations().size());
                        
                        // Execute healing actions
                        return healingEngine.executeHealingActions(analysisResult.getRecommendations());
                    })
                    .thenApply(healingResults -> {
                        logger.info("Automated healing completed with {} results", healingResults.size());
                        return ResponseEntity.ok(ApiResponse.success(healingResults));
                    })
                    .exceptionally(throwable -> {
                        logger.error("Error during automated healing", throwable);
                        return ResponseEntity.internalServerError().body(
                                ApiResponse.error("HEALING_ERROR", 
                                        "Failed to execute automated healing: " + throwable.getMessage()));
                    });
            
        } catch (Exception e) {
            logger.error("Error triggering automated healing", e);
            return CompletableFuture.completedFuture(
                    ResponseEntity.internalServerError().body(
                            ApiResponse.error("HEALING_ERROR", 
                                    "Failed to trigger automated healing: " + e.getMessage())));
        }
    }
    
    /**
     * Get healing execution history
     */
    @GetMapping("/healing-history")
    public ResponseEntity<ApiResponse<List<AutomatedHealingEngine.HealingExecution>>> 
            getHealingHistory(@RequestParam(defaultValue = "10") int limit) {
        
        logger.info("GET /api/v1/ai/healing-history - Retrieving healing history (limit: {})", limit);
        
        try {
            List<AutomatedHealingEngine.HealingExecution> history = healingEngine.getExecutionHistory(limit);
            
            logger.info("Successfully retrieved {} healing executions", history.size());
            
            return ResponseEntity.ok(ApiResponse.success(history));
            
        } catch (Exception e) {
            logger.error("Error retrieving healing history", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("HISTORY_ERROR", 
                            "Failed to retrieve healing history: " + e.getMessage()));
        }
    }
    
    /**
     * Get AI and healing system statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAIStatistics() {
        
        logger.info("GET /api/v1/ai/statistics - Retrieving AI system statistics");
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // Healing engine statistics
            statistics.put("healingEngine", healingEngine.getExecutionStatistics());
            
            // Error pattern statistics
            statistics.put("errorPatterns", errorPatternDetector.getErrorStatistics());
            
            // System health
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> systemHealth = new HashMap<>();
            systemHealth.put("memoryUsage", (runtime.totalMemory() - runtime.freeMemory()) / (double) runtime.maxMemory());
            systemHealth.put("availableProcessors", runtime.availableProcessors());
            statistics.put("systemHealth", systemHealth);
            
            // Timestamp
            statistics.put("timestamp", LocalDateTime.now());
            
            logger.info("Successfully retrieved AI system statistics");
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("Error retrieving AI statistics", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("STATISTICS_ERROR", 
                            "Failed to retrieve AI statistics: " + e.getMessage()));
        }
    }
    
    /**
     * Test AI analysis with custom error scenario
     */
    @PostMapping("/test-analysis")
    public CompletableFuture<ResponseEntity<ApiResponse<GenAIAnalysisService.AIAnalysisResult>>> 
            testAIAnalysis(@RequestParam String errorType, 
                          @RequestParam String errorMessage,
                          @RequestParam(defaultValue = "MEDIUM") String severity) {
        
        logger.info("POST /api/v1/ai/test-analysis - Testing AI analysis with custom scenario");
        
        try {
            // Create test error insight
            ErrorPatternDetector.ErrorInsight testInsight = new ErrorPatternDetector.ErrorInsight();
            testInsight.setType(errorType);
            testInsight.setMessage(errorMessage);
            testInsight.setSeverity(severity);
            testInsight.setOccurrenceCount(5);
            testInsight.setRecommendation("Test recommendation for " + errorType);
            
            List<ErrorPatternDetector.ErrorInsight> testInsights = List.of(testInsight);
            Map<String, Object> systemContext = buildSystemContext();
            
            return genAIAnalysisService.analyzeErrorPatterns(testInsights, systemContext)
                    .thenApply(result -> {
                        logger.info("Test AI analysis completed");
                        return ResponseEntity.ok(ApiResponse.success(result));
                    })
                    .exceptionally(throwable -> {
                        logger.error("Error during test AI analysis", throwable);
                        return ResponseEntity.internalServerError().body(
                                ApiResponse.error("TEST_ANALYSIS_ERROR", 
                                        "Failed to complete test analysis: " + throwable.getMessage()));
                    });
            
        } catch (Exception e) {
            logger.error("Error triggering test AI analysis", e);
            return CompletableFuture.completedFuture(
                    ResponseEntity.internalServerError().body(
                            ApiResponse.error("TEST_ANALYSIS_ERROR", 
                                    "Failed to trigger test analysis: " + e.getMessage())));
        }
    }
    
    /**
     * Get AI system health and configuration
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAIHealth() {
        
        logger.info("GET /api/v1/ai/health - Checking AI system health");
        
        try {
            Map<String, Object> health = new HashMap<>();
            
            // AI service status
            health.put("aiAnalysisService", "OPERATIONAL");
            health.put("healingEngine", "OPERATIONAL");
            health.put("errorPatternDetector", "OPERATIONAL");
            
            // Configuration
            health.put("configuration", Map.of(
                    "aiEnabled", true,
                    "mockMode", true,
                    "automationEnabled", true,
                    "dryRunMode", false
            ));
            
            // Recent activity
            health.put("recentActivity", Map.of(
                    "lastAnalysis", "Available on demand",
                    "lastHealing", "Available on demand",
                    "totalPatterns", errorPatternDetector.getErrorStatistics().getTotalPatterns()
            ));
            
            health.put("status", "HEALTHY");
            health.put("timestamp", LocalDateTime.now());
            
            logger.info("AI system health check completed");
            
            return ResponseEntity.ok(ApiResponse.success(health));
            
        } catch (Exception e) {
            logger.error("Error checking AI system health", e);
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("HEALTH_CHECK_ERROR", 
                            "Failed to check AI system health: " + e.getMessage()));
        }
    }
    
    /**
     * Build comprehensive system context for AI analysis
     */
    private Map<String, Object> buildSystemContext() {
        Map<String, Object> context = new HashMap<>();
        
        // Runtime information
        Runtime runtime = Runtime.getRuntime();
        context.put("memory", Map.of(
                "total", runtime.totalMemory(),
                "free", runtime.freeMemory(),
                "used", runtime.totalMemory() - runtime.freeMemory(),
                "max", runtime.maxMemory()
        ));
        
        context.put("processors", runtime.availableProcessors());
        context.put("timestamp", LocalDateTime.now());
        
        // Error statistics
        context.put("errorStatistics", errorPatternDetector.getErrorStatistics());
        
        // System characteristics
        context.put("systemType", "User Management Microservice");
        context.put("storageType", "JSON File-based");
        context.put("environment", "Development POC");
        context.put("scalingTarget", "1000 users, 10 concurrent requests");
        
        return context;
    }
}