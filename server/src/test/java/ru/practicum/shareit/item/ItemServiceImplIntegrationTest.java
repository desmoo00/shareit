package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

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
        // Подготовка данных
        UserDto userDto = new UserDto(null, "Test User", "test@mail.com");
        UserDto savedUser = userService.createUser(userDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Обычная дрель", true, null);

        // Вызов реального метода
        ItemDto savedItem = itemService.addItem(savedUser.getId(), itemDto);

        // Проверка в базе
        List<ItemDto> items = itemService.getUserItems(savedUser.getId());
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(savedItem.getId(), items.get(0).getId());
        assertEquals("Дрель", items.get(0).getName());
    }
}
