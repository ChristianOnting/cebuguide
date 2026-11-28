package com.cebuguide.backend.exception;

/** A request that conflicts with existing data, such as a duplicate name (returned as HTTP 409). */
public class ConflictException extends RuntimeException {

    private final String field;

    public ConflictException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
