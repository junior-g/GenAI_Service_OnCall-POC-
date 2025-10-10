package com.tata.self_healing.config;

import com.tata.self_healing.monitoring.RequestMonitoringInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for monitoring components
 */
@Configuration
public class MonitoringConfig implements WebMvcConfigurer {
    
    @Autowired
    private RequestMonitoringInterceptor requestMonitoringInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestMonitoringInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/actuator/**");
    }
}