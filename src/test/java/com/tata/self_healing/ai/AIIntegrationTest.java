package com.tata.self_healing.ai;

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
 * Integration tests for AI-powered self-healing functionality
 */
@SpringBootTest(classes = SelfHealingApplication.class)
@AutoConfigureMockMvc
public class AIIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private GenAIAnalysisService genAIAnalysisService;
    
    @Autowired
    private AutomatedHealingEngine healingEngine;
    
    @Test
    public void testAIAnalysisEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/ai/analyze")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.analysisId").exists())
                .andExpect(jsonPath("$.data.rootCauseAnalysis").exists())
                .andExpect(jsonPath("$.data.recommendations").isArray());
    }
    
    @Test
    public void testAutomatedHealingEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/ai/heal")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    public void testHealingHistoryEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/ai/healing-history")
                .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    public void testAIStatisticsEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/ai/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.healingEngine").exists())
                .andExpect(jsonPath("$.data.errorPatterns").exists());
    }
    
    @Test
    public void testAIHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/ai/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("HEALTHY"))
                .andExpect(jsonPath("$.data.aiAnalysisService").value("OPERATIONAL"));
    }
    
    @Test
    public void testCustomAIAnalysis() throws Exception {
        mockMvc.perform(post("/api/v1/ai/test-analysis")
                .param("errorType", "TEST_ERROR")
                .param("errorMessage", "Test error for AI analysis")
                .param("severity", "HIGH")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.confidence").exists());
    }
}