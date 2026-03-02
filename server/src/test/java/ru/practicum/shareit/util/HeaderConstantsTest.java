package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderConstantsTest {

    @Test
    void shouldExposeUserIdHeaderConstant() {
        new HeaderConstants();
        assertEquals("X-Sharer-User-Id", HeaderConstants.USER_ID_HEADER);
        assertEquals("X-Sharer-User-Id", HeaderConstants.USER_ID_HEADER);
    }
}
