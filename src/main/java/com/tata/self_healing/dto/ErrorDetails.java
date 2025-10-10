package com.tata.self_healing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorDetails {
    private String code;
    private String message;
    private Object details;

    public ErrorDetails(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorDetails(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}