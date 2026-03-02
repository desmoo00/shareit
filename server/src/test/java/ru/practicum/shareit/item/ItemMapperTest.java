package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {

    @Test
    void toItemDto_ShouldMapRequestId_WhenRequestExists() {
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);
        ItemRequest request = new ItemRequest();
        request.setId(10L);
        item.setRequest(request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(10L, dto.getRequestId());
    }

    @Test
    void toItemDto_ShouldMapNullRequestId_WhenRequestMissing() {
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("desc");
        item.setAvailable(true);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNull(dto.getRequestId());
    }

    @Test
    void updateItemFromDto_ShouldUpdateOnlyNonNullFields() {
        Item item = new Item();
        item.setName("old");
        item.setDescription("old");
        item.setAvailable(true);

        ItemMapper.updateItemFromDto(new ItemDto(null, null, "new", null, null), item);

        assertEquals("old", item.getName());
        assertEquals("new", item.getDescription());
        assertEquals(true, item.getAvailable());
    }
}
