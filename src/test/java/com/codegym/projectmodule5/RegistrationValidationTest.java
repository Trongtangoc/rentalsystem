package com.codegym.projectmodule5;

import com.codegym.projectmodule5.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRegistrationRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhone("+1234567890");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid registration request should have no violations");
    }

    @Test
    void testPasswordTooShort() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhone("+1234567890");
        request.setPassword("12345"); // Only 5 characters - should fail
        request.setConfirmPassword("12345");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Password shorter than 6 characters should fail validation");
        
        boolean hasPasswordViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password") && 
                      v.getMessage().contains("6-32 characters"));
        assertTrue(hasPasswordViolation, "Should have password length violation");
    }

    @Test
    void testPasswordTooLong() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhone("+1234567890");
        request.setPassword("a".repeat(33)); // 33 characters - should fail
        request.setConfirmPassword("a".repeat(33));

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Password longer than 32 characters should fail validation");
        
        boolean hasPasswordViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password") && 
                      v.getMessage().contains("6-32 characters"));
        assertTrue(hasPasswordViolation, "Should have password length violation");
    }

    @Test
    void testPasswordAtMinimumLength() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhone("+1234567890");
        request.setPassword("123456"); // Exactly 6 characters - should pass
        request.setConfirmPassword("123456");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        boolean hasPasswordViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        assertFalse(hasPasswordViolation, "Password with exactly 6 characters should pass validation");
    }

    @Test
    void testPasswordAtMaximumLength() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhone("+1234567890");
        request.setPassword("a".repeat(32)); // Exactly 32 characters - should pass
        request.setConfirmPassword("a".repeat(32));

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        boolean hasPasswordViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        assertFalse(hasPasswordViolation, "Password with exactly 32 characters should pass validation");
    }

    @Test
    void testPasswordMismatch() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPhone("+1234567890");
        request.setPassword("password123");
        request.setConfirmPassword("differentpassword");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Mismatched passwords should fail validation");
        
        boolean hasPasswordMismatchViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Passwords do not match"));
        assertTrue(hasPasswordMismatchViolation, "Should have password mismatch violation");
    }
}