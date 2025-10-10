package com.tata.self_healing.monitoring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

/**
 * Interceptor to monitor HTTP requests and collect metrics
 */
@Component
public class RequestMonitoringInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestMonitoringInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Autowired
    private MetricsCollector metricsCollector;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Instant startTime = Instant.now();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        metricsCollector.incrementRequestCount();
        
        logger.info("Request started: {} {} from IP: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                getClientIpAddress(request));
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        
        Instant startTime = (Instant) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            Duration duration = Duration.between(startTime, Instant.now());
            metricsCollector.recordRequestTime(duration);
            
            String status = getStatusCategory(response.getStatus());
            
            logger.info("Request completed: {} {} - Status: {} - Duration: {}ms", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    response.getStatus(),
                    duration.toMillis());
            
            // Track errors
            if (response.getStatus() >= 400) {
                metricsCollector.incrementErrorCount();
                metricsCollector.incrementErrorByType(status);
                
                logger.warn("Request failed: {} {} - Status: {} - Duration: {}ms", 
                        request.getMethod(), 
                        request.getRequestURI(), 
                        response.getStatus(),
                        duration.toMillis());
            }
        }
        
        if (ex != null) {
            logger.error("Request exception: {} {} - Exception: {}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    ex.getMessage(), ex);
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getStatusCategory(int status) {
        if (status >= 200 && status < 300) return "2xx_success";
        if (status >= 300 && status < 400) return "3xx_redirect";
        if (status >= 400 && status < 500) return "4xx_client_error";
        if (status >= 500) return "5xx_server_error";
        return "unknown";
    }
}