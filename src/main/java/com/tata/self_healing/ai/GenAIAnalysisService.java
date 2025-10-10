package com.tata.self_healing.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.self_healing.monitoring.ErrorPatternDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * GenAI-powered analysis service for intelligent error analysis and healing recommendations
 */
@Service
public class GenAIAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(GenAIAnalysisService.class);
    
    @Autowired
    private ServiceContextProvider serviceContextProvider;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${self-healing.ai.enabled:true}")
    private boolean aiEnabled;
    
    @Value("${self-healing.ai.mock-mode:true}")
    private boolean mockMode;
    
    @Value("${jamvant.ollama.api.url:http://localhost:11434/api/chat}")
    private String ollamaApiUrl;
    
    @Value("${jamvant.model.version:jamvant:v5.0}")
    private String jamvantModelVersion;
    
    @Value("${jamvant.request.timeout:30000}")
    private int requestTimeoutMs;
    
    /**
     * Analyze error patterns using GenAI and provide intelligent recommendations
     */
    public CompletableFuture<AIAnalysisResult> analyzeErrorPatterns(
            List<ErrorPatternDetector.ErrorInsight> insights,
            Map<String, Object> systemContext) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting GenAI analysis for {} error insights", insights.size());
                
                if (!aiEnabled) {
                    return createDisabledResult();
                }
                
                // Prepare context for LLM
                String analysisPrompt = buildAnalysisPrompt(insights, systemContext);
                
                // Call LLM (mock or real)
                String llmResponse = mockMode ? 
                    generateMockLLMResponse(insights, systemContext) : 
                    callRealLLM(analysisPrompt);
                
                // Parse and structure the response
                AIAnalysisResult result = parseAIResponse(llmResponse, insights);
                
                logger.info("GenAI analysis completed with {} recommendations", 
                    result.getRecommendations().size());
                
                return result;
                
            } catch (Exception e) {
                logger.error("Error during GenAI analysis", e);
                return createErrorResult(e.getMessage());
            }
        });
    }
    
    /**
     * Build comprehensive analysis prompt for LLM
     */
    private String buildAnalysisPrompt(List<ErrorPatternDetector.ErrorInsight> insights, 
                                     Map<String, Object> systemContext) {
        
        StringBuilder prompt = new StringBuilder();
        
        // Add service context
        prompt.append(serviceContextProvider.getServiceContext()).append("\n\n");
        
        // Add current system state
        prompt.append("## Current System State\n");
        prompt.append("Timestamp: ").append(LocalDateTime.now()).append("\n");
        prompt.append("System Context: ").append(systemContext.toString()).append("\n\n");
        
        // Add error insights
        prompt.append("## Error Patterns Detected\n");
        for (int i = 0; i < insights.size(); i++) {
            ErrorPatternDetector.ErrorInsight insight = insights.get(i);
            prompt.append(String.format("%d. Error Type: %s\n", i + 1, insight.getType()));
            prompt.append(String.format("   Severity: %s\n", insight.getSeverity()));
            prompt.append(String.format("   Message: %s\n", insight.getMessage()));
            prompt.append(String.format("   Occurrences: %d\n", insight.getOccurrenceCount()));
            prompt.append(String.format("   Current Recommendation: %s\n\n", insight.getRecommendation()));
        }
        
        // Add analysis request
        prompt.append("""
            ## Analysis Request
            As an AI specialist in self-healing systems, please analyze the above error patterns and provide:
            
            1. **Root Cause Analysis**: Identify the underlying causes of these error patterns
            2. **Business Impact Assessment**: Evaluate the impact on user experience and system reliability
            3. **Correlation Analysis**: Identify relationships between different error patterns
            4. **Healing Recommendations**: Provide specific, actionable healing strategies
            5. **Prevention Strategies**: Suggest long-term improvements to prevent recurrence
            6. **Automation Opportunities**: Identify which healing actions can be automated
            
            Focus on the business context of user management and the technical constraints of file-based storage.
            Prioritize data integrity, service availability, and user experience.
            
            Please provide your response in structured JSON format with the following schema:
            {
                "rootCauseAnalysis": "detailed analysis",
                "businessImpact": "impact assessment",
                "correlations": ["correlation1", "correlation2"],
                "healingRecommendations": [
                    {
                        "action": "specific action",
                        "priority": "HIGH|MEDIUM|LOW",
                        "automated": true/false,
                        "implementation": "how to implement",
                        "expectedOutcome": "expected result"
                    }
                ],
                "preventionStrategies": ["strategy1", "strategy2"],
                "automationOpportunities": ["opportunity1", "opportunity2"],
                "confidence": 0.95
            }
            """);
        
        return prompt.toString();
    }
    
    /**
     * Generate mock LLM response for testing (simulates real AI analysis)
     */
    private String generateMockLLMResponse(List<ErrorPatternDetector.ErrorInsight> insights, 
                                         Map<String, Object> systemContext) {
        
        logger.info("Generating mock AI response for {} insights", insights.size());
        
        // Analyze patterns to generate realistic mock response
        boolean hasValidationErrors = insights.stream()
            .anyMatch(i -> "VALIDATION_ERROR".equals(i.getType()));
        boolean hasHighErrorRate = insights.stream()
            .anyMatch(i -> "HIGH_ERROR_RATE".equals(i.getType()));
        boolean hasFileErrors = insights.stream()
            .anyMatch(i -> "FILE_OPERATION_ERROR".equals(i.getType()));
        
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        
        // Root cause analysis
        response.append("  \"rootCauseAnalysis\": \"");
        if (hasValidationErrors && hasHighErrorRate) {
            response.append("Analysis indicates a cascade failure pattern. High validation error rates suggest either client-side validation bypass or API misuse. The correlation with overall error rate indicates this may be affecting user experience significantly. File-based storage constraints may be amplifying the impact during concurrent access patterns.");
        } else if (hasFileErrors) {
            response.append("File operation errors detected indicate potential infrastructure issues. Given the JSON file-based storage architecture, this could be related to disk I/O performance, file locking contention, or storage capacity constraints. This is critical for data integrity.");
        } else {
            response.append("Error patterns suggest normal operational issues that can be addressed through improved error handling and user guidance. The patterns are within expected ranges for a user management system.");
        }
        response.append("\",\n");
        
        // Business impact
        response.append("  \"businessImpact\": \"");
        if (hasHighErrorRate) {
            response.append("HIGH - Error rates above threshold indicate degraded user experience. User registration and profile management operations are likely failing, directly impacting core business functionality. Immediate attention required to prevent user churn.");
        } else {
            response.append("MEDIUM - Current error patterns are manageable but require monitoring. User experience may be slightly degraded, but core functionality remains operational. Proactive improvements recommended.");
        }
        response.append("\",\n");
        
        // Correlations
        response.append("  \"correlations\": [");
        List<String> correlations = new ArrayList<>();
        if (hasValidationErrors) {
            correlations.add("\"Validation errors correlate with client-side integration issues\"");
        }
        if (hasHighErrorRate) {
            correlations.add("\"High error rate correlates with increased system load\"");
        }
        if (hasFileErrors) {
            correlations.add("\"File operation errors correlate with concurrent access patterns\"");
        }
        response.append(String.join(", ", correlations));
        response.append("],\n");
        
        // Healing recommendations
        response.append("  \"healingRecommendations\": [\n");
        List<String> recommendations = new ArrayList<>();
        
        if (hasValidationErrors) {
            recommendations.add("""
                {
                  "action": "Enhance input validation and error messaging",
                  "priority": "HIGH",
                  "automated": true,
                  "implementation": "Add client-side validation, improve API error responses with field-specific messages, implement request sanitization",
                  "expectedOutcome": "Reduce validation errors by 70%, improve user experience"
                }""");
        }
        
        if (hasFileErrors) {
            recommendations.add("""
                {
                  "action": "Implement file operation resilience",
                  "priority": "CRITICAL",
                  "automated": true,
                  "implementation": "Add retry mechanisms with exponential backoff, implement file backup before writes, monitor disk space",
                  "expectedOutcome": "Eliminate data loss risk, reduce file operation errors by 90%"
                }""");
        }
        
        if (hasHighErrorRate) {
            recommendations.add("""
                {
                  "action": "Implement circuit breaker pattern",
                  "priority": "HIGH",
                  "automated": true,
                  "implementation": "Add circuit breaker for file operations, implement graceful degradation with cached responses",
                  "expectedOutcome": "Prevent cascade failures, maintain service availability during issues"
                }""");
        }
        
        recommendations.add("""
            {
              "action": "Enhance monitoring and alerting",
              "priority": "MEDIUM",
              "automated": false,
              "implementation": "Set up proactive alerts for error rate thresholds, implement health check endpoints, add performance monitoring",
              "expectedOutcome": "Faster issue detection and resolution, improved system observability"
            }""");
        
        response.append(String.join(",\n    ", recommendations));
        response.append("\n  ],\n");
        
        // Prevention strategies
        response.append("  \"preventionStrategies\": [\n");
        response.append("    \"Implement comprehensive input validation at API gateway level\",\n");
        response.append("    \"Add automated testing for concurrent file operations\",\n");
        response.append("    \"Implement proactive disk space monitoring and cleanup\",\n");
        response.append("    \"Add load testing to identify performance bottlenecks\",\n");
        response.append("    \"Implement database migration strategy for production scalability\"\n");
        response.append("  ],\n");
        
        // Automation opportunities
        response.append("  \"automationOpportunities\": [\n");
        response.append("    \"Automated disk cleanup and log rotation\",\n");
        response.append("    \"Self-healing file corruption detection and recovery\",\n");
        response.append("    \"Automated scaling based on error rate thresholds\",\n");
        response.append("    \"Intelligent retry mechanisms with adaptive backoff\",\n");
        response.append("    \"Automated performance optimization based on usage patterns\"\n");
        response.append("  ],\n");
        
        // Confidence score
        double confidence = 0.85 + (Math.random() * 0.1); // 0.85-0.95
        response.append(String.format("  \"confidence\": %.2f\n", confidence));
        
        response.append("}");
        
        return response.toString();
    }
    
    /**
     * Call JAMVANT via Ollama API for real AI analysis
     */
    private String callRealLLM(String prompt) {
        try {
            logger.info("Calling JAMVANT via Ollama API: {}", ollamaApiUrl);
            
            // Prepare JAMVANT request with proper formatting
            Map<String, Object> request = Map.of(
                "model", jamvantModelVersion,
                "messages", List.of(Map.of(
                    "role", "user",
                    "content", "Hey! JAMVANT, " + prompt + " Provide analysis in JSON format."
                )),
                "stream", false
            );
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "JAMVANT-SH-SBUMM-POC/1.0");
            
            // Create HTTP entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            // Call Ollama API with timeout handling
            ResponseEntity<String> response = restTemplate.exchange(
                ollamaApiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parse Ollama response to extract JAMVANT message content
                JsonNode responseNode = objectMapper.readTree(response.getBody());
                String jamvantResponse = responseNode.path("message").path("content").asText();
                
                if (jamvantResponse != null && !jamvantResponse.trim().isEmpty()) {
                    // Clean up JAMVANT response - extract JSON if it's mixed with text
                    String cleanedResponse = extractJsonFromResponse(jamvantResponse);
                    
                    logger.info("JAMVANT analysis completed successfully via Ollama API");
                    logger.debug("JAMVANT response length: {} characters", cleanedResponse.length());
                    return cleanedResponse;
                } else {
                    throw new RuntimeException("Empty response from JAMVANT");
                }
            } else {
                throw new RuntimeException("Ollama API returned status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("Error calling JAMVANT via Ollama API: {}", e.getMessage(), e);
            logger.warn("Falling back to mock response due to JAMVANT integration error");
            
            // Graceful fallback to mock response
            return generateMockLLMResponse(Collections.emptyList(), Collections.emptyMap());
        }
    }
    
    /**
     * Extract JSON from JAMVANT response, handling cases where response might include text before JSON
     */
    private String extractJsonFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return response;
        }
        
        // Look for JSON object start
        int jsonStart = response.indexOf('{');
        if (jsonStart == -1) {
            return response; // No JSON found, return as is
        }
        
        // Find the matching closing brace
        int braceCount = 0;
        int jsonEnd = -1;
        
        for (int i = jsonStart; i < response.length(); i++) {
            char c = response.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    jsonEnd = i;
                    break;
                }
            }
        }
        
        if (jsonEnd != -1) {
            String extractedJson = response.substring(jsonStart, jsonEnd + 1);
            logger.debug("Extracted JSON from JAMVANT response");
            return extractedJson;
        }
        
        return response; // Return original if JSON extraction fails
    }
    
    /**
     * Parse AI response into structured result
     */
    private AIAnalysisResult parseAIResponse(String llmResponse, 
                                           List<ErrorPatternDetector.ErrorInsight> originalInsights) {
        try {
            // Parse JSON response
            Map<String, Object> responseMap = objectMapper.readValue(llmResponse, Map.class);
            
            AIAnalysisResult result = new AIAnalysisResult();
            result.setAnalysisId(UUID.randomUUID().toString());
            result.setTimestamp(LocalDateTime.now());
            result.setRootCauseAnalysis((String) responseMap.get("rootCauseAnalysis"));
            result.setBusinessImpact((String) responseMap.get("businessImpact"));
            result.setCorrelations((List<String>) responseMap.get("correlations"));
            result.setPreventionStrategies((List<String>) responseMap.get("preventionStrategies"));
            result.setAutomationOpportunities((List<String>) responseMap.get("automationOpportunities"));
            result.setConfidence(((Number) responseMap.get("confidence")).doubleValue());
            
            // Parse recommendations
            List<Map<String, Object>> recList = (List<Map<String, Object>>) responseMap.get("healingRecommendations");
            List<HealingRecommendation> recommendations = new ArrayList<>();
            
            for (Map<String, Object> recMap : recList) {
                HealingRecommendation rec = new HealingRecommendation();
                rec.setAction((String) recMap.get("action"));
                rec.setPriority((String) recMap.get("priority"));
                rec.setAutomated((Boolean) recMap.get("automated"));
                rec.setImplementation((String) recMap.get("implementation"));
                rec.setExpectedOutcome((String) recMap.get("expectedOutcome"));
                rec.setRecommendationId(UUID.randomUUID().toString());
                recommendations.add(rec);
            }
            
            result.setRecommendations(recommendations);
            result.setOriginalInsights(originalInsights);
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error parsing AI response", e);
            return createErrorResult("Failed to parse AI response: " + e.getMessage());
        }
    }
    
    private AIAnalysisResult createDisabledResult() {
        AIAnalysisResult result = new AIAnalysisResult();
        result.setAnalysisId("disabled");
        result.setTimestamp(LocalDateTime.now());
        result.setRootCauseAnalysis("AI analysis is disabled");
        result.setBusinessImpact("N/A");
        result.setConfidence(0.0);
        result.setRecommendations(Collections.emptyList());
        return result;
    }
    
    private AIAnalysisResult createErrorResult(String errorMessage) {
        AIAnalysisResult result = new AIAnalysisResult();
        result.setAnalysisId("error");
        result.setTimestamp(LocalDateTime.now());
        result.setRootCauseAnalysis("Error during analysis: " + errorMessage);
        result.setBusinessImpact("Unable to assess");
        result.setConfidence(0.0);
        result.setRecommendations(Collections.emptyList());
        return result;
    }
    
    // Inner classes for structured results
    public static class AIAnalysisResult {
        private String analysisId;
        private LocalDateTime timestamp;
        private String rootCauseAnalysis;
        private String businessImpact;
        private List<String> correlations;
        private List<HealingRecommendation> recommendations;
        private List<String> preventionStrategies;
        private List<String> automationOpportunities;
        private double confidence;
        private List<ErrorPatternDetector.ErrorInsight> originalInsights;
        
        // Getters and setters
        public String getAnalysisId() { return analysisId; }
        public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getRootCauseAnalysis() { return rootCauseAnalysis; }
        public void setRootCauseAnalysis(String rootCauseAnalysis) { this.rootCauseAnalysis = rootCauseAnalysis; }
        public String getBusinessImpact() { return businessImpact; }
        public void setBusinessImpact(String businessImpact) { this.businessImpact = businessImpact; }
        public List<String> getCorrelations() { return correlations; }
        public void setCorrelations(List<String> correlations) { this.correlations = correlations; }
        public List<HealingRecommendation> getRecommendations() { return recommendations; }
        public void setRecommendations(List<HealingRecommendation> recommendations) { this.recommendations = recommendations; }
        public List<String> getPreventionStrategies() { return preventionStrategies; }
        public void setPreventionStrategies(List<String> preventionStrategies) { this.preventionStrategies = preventionStrategies; }
        public List<String> getAutomationOpportunities() { return automationOpportunities; }
        public void setAutomationOpportunities(List<String> automationOpportunities) { this.automationOpportunities = automationOpportunities; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public List<ErrorPatternDetector.ErrorInsight> getOriginalInsights() { return originalInsights; }
        public void setOriginalInsights(List<ErrorPatternDetector.ErrorInsight> originalInsights) { this.originalInsights = originalInsights; }
    }
    
    public static class HealingRecommendation {
        private String recommendationId;
        private String action;
        private String priority;
        private boolean automated;
        private String implementation;
        private String expectedOutcome;
        
        // Getters and setters
        public String getRecommendationId() { return recommendationId; }
        public void setRecommendationId(String recommendationId) { this.recommendationId = recommendationId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public boolean isAutomated() { return automated; }
        public void setAutomated(boolean automated) { this.automated = automated; }
        public String getImplementation() { return implementation; }
        public void setImplementation(String implementation) { this.implementation = implementation; }
        public String getExpectedOutcome() { return expectedOutcome; }
        public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
    }
}