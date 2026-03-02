package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void shouldSaveAndGetItem() {
        UserDto user = userService.createUser(new UserDto(null, "Test User", "test@mail.com"));
        ItemDto saved = itemService.addItem(user.getId(), new ItemDto(null, "Drill", "Simple drill", true, null));

        List<ItemDto> items = itemService.getUserItems(user.getId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(saved.getId(), items.get(0).getId());
        assertEquals("Drill", items.get(0).getName());
    }

    @Test
    void shouldUpdateItem() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner-item@mail.com"));
        ItemDto savedItem = itemService.addItem(owner.getId(), new ItemDto(null, "Hammer", "Old", true, null));

        ItemDto updateDto = new ItemDto(null, "Hammer", "New description", true, null);
        ItemDto updated = itemService.updateItem(owner.getId(), savedItem.getId(), updateDto);

        assertEquals("New description", updated.getDescription());
    }

    @Test
    void shouldGetItemByIdWithoutUser() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner2", "owner2@mail.com"));
        ItemDto savedItem = itemService.addItem(owner.getId(), new ItemDto(null, "Saw", "Wood saw", true, null));

        ItemDto found = itemService.getItemById(savedItem.getId());

        assertEquals(savedItem.getId(), found.getId());
        assertEquals("Saw", found.getName());
    }

    @Test
    void shouldGetItemByIdForOwner() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner4", "owner4@mail.com"));
        ItemDto savedItem = itemService.addItem(owner.getId(), new ItemDto(null, "Wrench", "Metal wrench", true, null));

        ItemDto found = itemService.getItemById(owner.getId(), savedItem.getId());

        assertEquals(savedItem.getId(), found.getId());
        assertEquals("Wrench", found.getName());
    }

    @Test
    void shouldSearchItemsAndReturnEmptyForBlankText() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner3", "owner3@mail.com"));
        itemService.addItem(owner.getId(), new ItemDto(null, "Drill Pro", "Powerful tool", true, null));

        List<ItemDto> found = itemService.searchItems("drill");
        assertEquals(1, found.size());

        assertTrue(itemService.searchItems(" ").isEmpty());
        assertTrue(itemService.searchItems(null).isEmpty());
    }

    @Test
    void shouldThrowValidationExceptionWhenAddCommentWithoutCompletedBooking() {
        UserDto owner = userService.createUser(new UserDto(null, "Item Owner", "item-owner@mail.com"));
        UserDto author = userService.createUser(new UserDto(null, "Author", "author@mail.com"));
        ItemDto item = itemService.addItem(owner.getId(), new ItemDto(null, "Bike", "City bike", true, null));

        CommentDto commentDto = new CommentDto(null, "Nice item", null, LocalDateTime.now());

        assertThrows(ValidationException.class, () -> itemService.addComment(author.getId(), item.getId(), commentDto));
    }
}
