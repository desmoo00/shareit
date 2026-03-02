package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void shouldHandleNotFound() {
        // 404: даменная ошибка "не найдено".
        ErrorResponse response = errorHandler.handleNotFound(new NotFoundException("not found"));
        assertEquals("not found", response.getError());
    }

    @Test
    void shouldHandleNotOwner() {
        // 403: действие недоступно не-владельцу.
        ErrorResponse response = errorHandler.handleNotOwner(new NotOwnerException("forbidden"));
        assertEquals("forbidden", response.getError());
    }

    @Test
    void shouldHandleConflict() {
        // 409: конфликт целостности/состояния.
        ErrorResponse response = errorHandler.handleConflict(new ConflictException("conflict"));
        assertEquals("conflict", response.getError());
    }

    @Test
    void shouldHandleValidation() {
        // 400: бизнес-ошибка валидации.
        ErrorResponse response = errorHandler.handleValidation(new ValidationException("bad request"));
        assertEquals("bad request", response.getError());
    }

    @Test
    void shouldHandleAnnotationValidation() {
        // 400: ошибка bean validation на уровне аннотаций.
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn("details");

        ErrorResponse response = errorHandler.handleAnnotationValidation(exception);
        assertEquals("Validation error: details", response.getError());
    }

    @Test
    void shouldHandleThrowable() {
        // 500: для неожиданных исключений возвращаем безопасный общий текст.
        ErrorResponse response = errorHandler.handleThrowable(new RuntimeException("boom"));
        assertEquals("Internal server error", response.getError());
    }

    @Test
    void shouldHandleMissingHeader() {
        MissingRequestHeaderException exception = new MissingRequestHeaderException("X-Test", null);
        ErrorResponse response = errorHandler.handleMissingRequestHeaderException(exception);
        assertEquals("Не передан заголовок: X-Test", response.getError());
    }

    @Test
    void shouldHandleDataIntegrityViolation() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("duplicate");
        ErrorResponse response = errorHandler.handleDataIntegrityViolationException(exception);
        assertEquals("Нарушение целостности данных: duplicate", response.getError());
    }
}
