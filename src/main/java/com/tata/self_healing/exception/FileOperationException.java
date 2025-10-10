package com.tata.self_healing.exception;

public class FileOperationException extends RuntimeException {
    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}