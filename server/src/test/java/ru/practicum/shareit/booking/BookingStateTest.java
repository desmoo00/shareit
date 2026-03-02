package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingStateTest {

    @Test
    void from_ShouldParseCaseInsensitiveValue() {
        Optional<BookingState> state = BookingState.from("all");

        assertTrue(state.isPresent());
        assertEquals(BookingState.ALL, state.get());
    }

    @Test
    void from_ShouldReturnEmpty_WhenUnknown() {
        assertTrue(BookingState.from("unknown").isEmpty());
    }
}
