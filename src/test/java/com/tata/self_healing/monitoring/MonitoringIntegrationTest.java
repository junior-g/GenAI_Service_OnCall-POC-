package com.tata.self_healing.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.self_healing.SelfHealingApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for monitoring functionality
 */
@SpringBootTest(classes = SelfHealingApplication.class)
@AutoConfigureMockMvc
public class MonitoringIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ErrorPatternDetector errorPatternDetector;
    
    @Test
    public void testErrorPatternsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/monitoring/error-patterns")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    public void testErrorStatisticsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/monitoring/error-statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPatterns").exists());
    }
    
    @Test
    public void testHealthMetricsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/monitoring/health-metrics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.memory").exists())
                .andExpect(jsonPath("$.data.processors").exists());
    }
    
    @Test
    public void testRecordTestError() throws Exception {
        mockMvc.perform(post("/api/v1/monitoring/test-error")
                .param("errorType", "TEST_ERROR")
                .param("errorMessage", "This is a test error")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    public void testTriggerPatternAnalysis() throws Exception {
        // First record some test errors
        errorPatternDetector.recordError("TEST_ERROR", "Test message", "Test stack", "test-id");
        
        mockMvc.perform(post("/api/v1/monitoring/analyze-patterns")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    public void testCorrelationIdInHeaders() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .header("X-Correlation-ID", "test-correlation-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Correlation-ID", "test-correlation-123"));
    }
}