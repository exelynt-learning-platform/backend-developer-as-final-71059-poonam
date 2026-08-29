package com.example.booking.exception;

public class AccessDeniedCustomException extends RuntimeException {

    public AccessDeniedCustomException(String message) {
        super(message);
    }
}
