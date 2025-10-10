package com.tata.self_healing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ErrorDetails error;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static ApiResponse<Void> success() {
        ApiResponse<Void> response = new ApiResponse<>();
        response.success = true;
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorDetails(code, message);
        return response;
    }

    public static <T> ApiResponse<T> error(String code, String message, Object details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorDetails(code, message, details);
        return response;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public ErrorDetails getError() { return error; }
    public LocalDateTime getTimestamp() { return timestamp; }
}