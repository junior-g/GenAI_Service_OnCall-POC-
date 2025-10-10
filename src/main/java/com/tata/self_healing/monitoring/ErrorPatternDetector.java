package com.tata.self_healing.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Detects error patterns and anomalies for self-healing analysis
 */
@Component
public class ErrorPatternDetector {
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorPatternDetector.class);
    
    // Error pattern tracking
    private final Map<String, ErrorPattern> errorPatterns = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> recentErrors = new ConcurrentHashMap<>();
    
    // Thresholds for pattern detection
    private static final int ERROR_THRESHOLD_COUNT = 5;
    private static final int ERROR_THRESHOLD_MINUTES = 10;
    private static final double ERROR_RATE_THRESHOLD = 0.1; // 10% error rate
    
    @Autowired
    private MetricsCollector metricsCollector;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Record an error occurrence for pattern analysis
     */
    public void recordError(String errorType, String errorMessage, String stackTrace, String correlationId) {
        ErrorOccurrence occurrence = new ErrorOccurrence(
                errorType, errorMessage, stackTrace, correlationId, LocalDateTime.now()
        );
        
        // Update error patterns
        String patternKey = generatePatternKey(errorType, errorMessage);
        ErrorPattern pattern = errorPatterns.computeIfAbsent(patternKey, 
                k -> new ErrorPattern(errorType, errorMessage));
        
        pattern.addOccurrence(occurrence);
        
        // Check for anomalies
        checkForAnomalies(pattern);
        
        // Update recent error counts
        updateRecentErrorCounts(errorType);
        
        logger.info("Error recorded for pattern analysis: type={}, pattern={}, correlationId={}", 
                errorType, patternKey, correlationId);
    }
    
    /**
     * Analyze current error patterns and return insights
     */
    public List<ErrorInsight> analyzePatterns() {
        List<ErrorInsight> insights = new ArrayList<>();
        
        for (ErrorPattern pattern : errorPatterns.values()) {
            if (pattern.isAnomalous()) {
                ErrorInsight insight = generateInsight(pattern);
                insights.add(insight);
            }
        }
        
        // Check for high error rates
        checkErrorRates(insights);
        
        return insights;
    }
    
    /**
     * Get error statistics for monitoring dashboard
     */
    public ErrorStatistics getErrorStatistics() {
        ErrorStatistics stats = new ErrorStatistics();
        
        stats.setTotalPatterns(errorPatterns.size());
        stats.setAnomalousPatterns((int) errorPatterns.values().stream()
                .filter(ErrorPattern::isAnomalous).count());
        
        // Calculate error rates by type
        Map<String, Integer> errorsByType = new HashMap<>();
        for (String errorType : recentErrors.keySet()) {
            errorsByType.put(errorType, recentErrors.get(errorType).get());
        }
        stats.setErrorsByType(errorsByType);
        
        return stats;
    }
    
    private void checkForAnomalies(ErrorPattern pattern) {
        LocalDateTime cutoff = LocalDateTime.now().minus(ERROR_THRESHOLD_MINUTES, ChronoUnit.MINUTES);
        long recentOccurrences = pattern.getOccurrences().stream()
                .filter(occ -> occ.getTimestamp().isAfter(cutoff))
                .count();
        
        if (recentOccurrences >= ERROR_THRESHOLD_COUNT) {
            pattern.setAnomalous(true);
            logger.warn("Anomalous error pattern detected: {} with {} occurrences in last {} minutes", 
                    pattern.getErrorType(), recentOccurrences, ERROR_THRESHOLD_MINUTES);
        }
    }
    
    private void updateRecentErrorCounts(String errorType) {
        recentErrors.computeIfAbsent(errorType, k -> new AtomicInteger(0)).incrementAndGet();
        
        // Clean up old counts periodically (simplified approach)
        if (recentErrors.size() > 100) {
            recentErrors.clear();
        }
    }
    
    private void checkErrorRates(List<ErrorInsight> insights) {
        // This would typically compare against total request count
        // For now, we'll use a simplified approach
        int totalErrors = recentErrors.values().stream()
                .mapToInt(AtomicInteger::get)
                .sum();
        
        if (totalErrors > ERROR_THRESHOLD_COUNT * 2) {
            ErrorInsight highErrorRate = new ErrorInsight();
            highErrorRate.setType("HIGH_ERROR_RATE");
            highErrorRate.setSeverity("HIGH");
            highErrorRate.setMessage("High error rate detected: " + totalErrors + " errors recently");
            highErrorRate.setRecommendation("Investigate system health and consider scaling or restarting services");
            insights.add(highErrorRate);
        }
    }
    
    private ErrorInsight generateInsight(ErrorPattern pattern) {
        ErrorInsight insight = new ErrorInsight();
        insight.setType(pattern.getErrorType());
        insight.setSeverity(determineSeverity(pattern));
        insight.setMessage("Repeated error pattern detected: " + pattern.getErrorMessage());
        insight.setOccurrenceCount(pattern.getOccurrences().size());
        insight.setRecommendation(generateRecommendation(pattern));
        
        return insight;
    }
    
    private String determineSeverity(ErrorPattern pattern) {
        int occurrences = pattern.getOccurrences().size();
        if (occurrences >= ERROR_THRESHOLD_COUNT * 3) return "CRITICAL";
        if (occurrences >= ERROR_THRESHOLD_COUNT * 2) return "HIGH";
        if (occurrences >= ERROR_THRESHOLD_COUNT) return "MEDIUM";
        return "LOW";
    }
    
    private String generateRecommendation(ErrorPattern pattern) {
        String errorType = pattern.getErrorType();
        
        switch (errorType) {
            case "VALIDATION_ERROR":
                return "Review input validation rules and client-side validation";
            case "FILE_OPERATION_ERROR":
                return "Check file system permissions and disk space";
            case "USER_NOT_FOUND":
                return "Verify data integrity and implement proper error handling";
            case "DUPLICATE_EMAIL":
                return "Implement better duplicate checking and user feedback";
            default:
                return "Investigate root cause and implement appropriate error handling";
        }
    }
    
    private String generatePatternKey(String errorType, String errorMessage) {
        return errorType + ":" + (errorMessage != null ? errorMessage.hashCode() : "null");
    }
    
    // Inner classes for data structures
    public static class ErrorOccurrence {
        private final String errorType;
        private final String errorMessage;
        private final String stackTrace;
        private final String correlationId;
        private final LocalDateTime timestamp;
        
        public ErrorOccurrence(String errorType, String errorMessage, String stackTrace, 
                             String correlationId, LocalDateTime timestamp) {
            this.errorType = errorType;
            this.errorMessage = errorMessage;
            this.stackTrace = stackTrace;
            this.correlationId = correlationId;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getErrorType() { return errorType; }
        public String getErrorMessage() { return errorMessage; }
        public String getStackTrace() { return stackTrace; }
        public String getCorrelationId() { return correlationId; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public static class ErrorPattern {
        private final String errorType;
        private final String errorMessage;
        private final List<ErrorOccurrence> occurrences = new ArrayList<>();
        private boolean anomalous = false;
        
        public ErrorPattern(String errorType, String errorMessage) {
            this.errorType = errorType;
            this.errorMessage = errorMessage;
        }
        
        public void addOccurrence(ErrorOccurrence occurrence) {
            occurrences.add(occurrence);
        }
        
        // Getters and setters
        public String getErrorType() { return errorType; }
        public String getErrorMessage() { return errorMessage; }
        public List<ErrorOccurrence> getOccurrences() { return occurrences; }
        public boolean isAnomalous() { return anomalous; }
        public void setAnomalous(boolean anomalous) { this.anomalous = anomalous; }
    }
    
    public static class ErrorInsight {
        private String type;
        private String severity;
        private String message;
        private int occurrenceCount;
        private String recommendation;
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getOccurrenceCount() { return occurrenceCount; }
        public void setOccurrenceCount(int occurrenceCount) { this.occurrenceCount = occurrenceCount; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    public static class ErrorStatistics {
        private int totalPatterns;
        private int anomalousPatterns;
        private Map<String, Integer> errorsByType;
        
        // Getters and setters
        public int getTotalPatterns() { return totalPatterns; }
        public void setTotalPatterns(int totalPatterns) { this.totalPatterns = totalPatterns; }
        public int getAnomalousPatterns() { return anomalousPatterns; }
        public void setAnomalousPatterns(int anomalousPatterns) { this.anomalousPatterns = anomalousPatterns; }
        public Map<String, Integer> getErrorsByType() { return errorsByType; }
        public void setErrorsByType(Map<String, Integer> errorsByType) { this.errorsByType = errorsByType; }
    }
}