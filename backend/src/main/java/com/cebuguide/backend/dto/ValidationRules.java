package com.cebuguide.backend.dto;

/** Shared validation rules, so every password field is checked the same way. */
public final class ValidationRules {

    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$";
    public static final String PASSWORD_MESSAGE = "Password must be 8 to 72 characters and contain both letters and numbers.";

    private ValidationRules() {
    }
}
