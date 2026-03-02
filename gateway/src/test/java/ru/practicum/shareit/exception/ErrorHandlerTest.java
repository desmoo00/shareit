package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void shouldHandleBadRequest() {
        ErrorResponse response = errorHandler.handleBadRequest(new IllegalArgumentException("bad request"));
        assertEquals("bad request", response.getError());
    }

    @Test
    void shouldHandleConstraintViolation() {
        ErrorResponse response = errorHandler.handleBadRequest(new ConstraintViolationException("constraint", null));
        assertEquals("constraint", response.getError());
    }

    @Test
    void shouldHandleUnexpected() {
        ErrorResponse response = errorHandler.handleUnexpected(new RuntimeException("boom"));
        assertEquals("Internal server error", response.getError());
    }
}
