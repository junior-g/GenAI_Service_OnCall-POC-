package com.tata.self_healing.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom metrics collector for self-healing system monitoring
 */
@Component
public class MetricsCollector {
    
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, AtomicLong> errorCounts = new ConcurrentHashMap<>();
    
    // Counters
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Counter validationErrorCounter;
    private final Counter fileOperationErrorCounter;
    
    // Timers
    private final Timer requestTimer;
    private final Timer fileOperationTimer;
    
    @Autowired
    public MetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.requestCounter = Counter.builder("self_healing.requests.total")
                .description("Total number of HTTP requests")
                .register(meterRegistry);
                
        this.errorCounter = Counter.builder("self_healing.errors.total")
                .description("Total number of errors")
                .register(meterRegistry);
                
        this.validationErrorCounter = Counter.builder("self_healing.validation_errors.total")
                .description("Total number of validation errors")
                .register(meterRegistry);
                
        this.fileOperationErrorCounter = Counter.builder("self_healing.file_operation_errors.total")
                .description("Total number of file operation errors")
                .register(meterRegistry);
        
        // Initialize timers
        this.requestTimer = Timer.builder("self_healing.request.duration")
                .description("Request processing time")
                .register(meterRegistry);
                
        this.fileOperationTimer = Timer.builder("self_healing.file_operation.duration")
                .description("File operation processing time")
                .register(meterRegistry);
    }
    
    public void incrementRequestCount() {
        requestCounter.increment();
    }
    
    public void incrementErrorCount() {
        errorCounter.increment();
    }
    
    public void incrementValidationErrorCount() {
        validationErrorCounter.increment();
    }
    
    public void incrementFileOperationErrorCount() {
        fileOperationErrorCounter.increment();
    }
    
    public void recordRequestTime(Duration duration) {
        requestTimer.record(duration);
    }
    
    public void recordFileOperationTime(Duration duration) {
        fileOperationTimer.record(duration);
    }
    
    public void incrementErrorByType(String errorType) {
        errorCounts.computeIfAbsent(errorType, k -> {
            AtomicLong counter = new AtomicLong(0);
            meterRegistry.gauge("self_healing.error_by_type", 
                    io.micrometer.core.instrument.Tags.of("type", errorType), 
                    counter, AtomicLong::get);
            return counter;
        }).incrementAndGet();
    }
    
    public long getErrorCountByType(String errorType) {
        return errorCounts.getOrDefault(errorType, new AtomicLong(0)).get();
    }
}