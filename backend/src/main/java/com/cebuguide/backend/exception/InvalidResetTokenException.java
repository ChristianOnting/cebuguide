package com.cebuguide.backend.exception;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("This password reset link is invalid or has expired. Please request a new one.");
    }
}
