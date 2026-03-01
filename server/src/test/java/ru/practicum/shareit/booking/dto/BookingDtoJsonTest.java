package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 2, 12, 0);

        BookingDto dto = new BookingDto(1L, start, end, 2L, null, null, BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-01-02T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
