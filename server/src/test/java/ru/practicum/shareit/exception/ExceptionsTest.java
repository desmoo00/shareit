package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionsTest {

    @Test
    void constructors_ShouldStoreMessage() {
        assertEquals("c", new ConflictException("c").getMessage());
        assertEquals("n", new NotFoundException("n").getMessage());
        assertEquals("o", new NotOwnerException("o").getMessage());
        assertEquals("v", new ValidationException("v").getMessage());
    }
}
