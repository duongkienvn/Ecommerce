package com.project.shopapp.exception;

public class UnauthenticationException extends RuntimeException {
    public UnauthenticationException(String message) {
        super(message);
    }
}
