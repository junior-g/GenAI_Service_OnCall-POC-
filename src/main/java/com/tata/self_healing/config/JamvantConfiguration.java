package com.tata.self_healing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for JAMVANT integration via Ollama API
 */
@Configuration
public class JamvantConfiguration {
    
    @Value("${jamvant.request.timeout:30000}")
    private int requestTimeoutMs;
    
    /**
     * RestTemplate bean configured for JAMVANT/Ollama API calls
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Configure timeout settings for JAMVANT calls using SimpleClientHttpRequestFactory
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds connection timeout
        factory.setReadTimeout(requestTimeoutMs); // Configurable read timeout (default 30s)
        
        restTemplate.setRequestFactory(factory);
        
        return restTemplate;
    }
}