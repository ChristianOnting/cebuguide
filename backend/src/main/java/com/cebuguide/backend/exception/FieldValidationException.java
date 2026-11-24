package com.cebuguide.backend.exception;

/** A business-rule validation error tied to one input field (returned as HTTP 400). */
public class FieldValidationException extends RuntimeException {

    private final String field;

    public FieldValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
