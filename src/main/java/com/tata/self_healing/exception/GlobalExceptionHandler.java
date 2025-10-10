package com.tata.self_healing.exception;

import com.tata.self_healing.dto.ApiResponse;
import com.tata.self_healing.monitoring.ErrorPatternDetector;
import com.tata.self_healing.monitoring.MetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Autowired
    private ErrorPatternDetector errorPatternDetector;
    
    @Autowired
    private MetricsCollector metricsCollector;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        String correlationId = MDC.get("correlationId");
        logger.error("User not found: {} [correlationId={}]", ex.getMessage(), correlationId);

        // Record error for pattern analysis
        errorPatternDetector.recordError("USER_NOT_FOUND", ex.getMessage(), 
                getStackTrace(ex), correlationId);
        metricsCollector.incrementErrorByType("USER_NOT_FOUND");

        ApiResponse<Void> response = ApiResponse.error("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmailException(DuplicateEmailException ex) {
        String correlationId = MDC.get("correlationId");
        logger.error("Duplicate email: {} [correlationId={}]", ex.getMessage(), correlationId);

        // Record error for pattern analysis
        errorPatternDetector.recordError("DUPLICATE_EMAIL", ex.getMessage(), 
                getStackTrace(ex), correlationId);
        metricsCollector.incrementErrorByType("DUPLICATE_EMAIL");

        ApiResponse<Void> response = ApiResponse.error("DUPLICATE_EMAIL", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileOperationException(FileOperationException ex) {
        String correlationId = MDC.get("correlationId");
        logger.error("File operation error: {} [correlationId={}]", ex.getMessage(), correlationId, ex);

        // Record error for pattern analysis
        errorPatternDetector.recordError("FILE_OPERATION_ERROR", ex.getMessage(), 
                getStackTrace(ex), correlationId);
        metricsCollector.incrementFileOperationErrorCount();
        metricsCollector.incrementErrorByType("FILE_OPERATION_ERROR");

        ApiResponse<Void> response = ApiResponse.error("FILE_OPERATION_ERROR",
                "An error occurred while accessing user data. Please try again.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String correlationId = MDC.get("correlationId");
        logger.error("Validation error: {} [correlationId={}]", ex.getMessage(), correlationId);

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        // Record error for pattern analysis
        String errorMessage = "Validation failed: " + validationErrors.toString();
        errorPatternDetector.recordError("VALIDATION_ERROR", errorMessage, 
                getStackTrace(ex), correlationId);
        metricsCollector.incrementValidationErrorCount();
        metricsCollector.incrementErrorByType("VALIDATION_ERROR");

        ApiResponse<Void> response = ApiResponse.error("VALIDATION_ERROR",
                "Invalid input data", validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        String correlationId = MDC.get("correlationId");
        logger.error("Unexpected error: {} [correlationId={}]", ex.getMessage(), correlationId, ex);

        // Record error for pattern analysis
        errorPatternDetector.recordError("INTERNAL_ERROR", ex.getMessage(), 
                getStackTrace(ex), correlationId);
        metricsCollector.incrementErrorByType("INTERNAL_ERROR");

        ApiResponse<Void> response = ApiResponse.error("INTERNAL_ERROR",
                "An unexpected error occurred. Please try again.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Helper method to get stack trace as string
     */
    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}