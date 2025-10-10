package com.tata.self_healing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Map;

public class HealthCheckController {

    @GetMapping("/users/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "User Service"
        );
        return ResponseEntity.ok(status);
    }

}
