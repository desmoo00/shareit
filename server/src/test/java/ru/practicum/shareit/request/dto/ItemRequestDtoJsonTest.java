package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerializeWithNullItems() throws Exception {
        LocalDateTime created = LocalDateTime.of(2026, 2, 25, 12, 0);
        // items = null, поле не должно попасть в JSON
        ItemRequestDto dto = new ItemRequestDto(1L, "Нужна пила", created, null);

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна пила");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-02-25T12:00:00");
        assertThat(result).doesNotHaveJsonPath("$.items");
    }

    @Test
    void testSerializeWithItems() throws Exception {
        LocalDateTime created = LocalDateTime.of(2026, 2, 25, 12, 0);
        List<ItemRequestItemDto> items = List.of(new ItemRequestItemDto(2L, "Пила ручная", 3L));
        ItemRequestDto dto = new ItemRequestDto(1L, "Нужна пила", created, items);

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathArrayValue("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Пила ручная");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(3);
    }
}
