package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateAndGetUser() {
        UserDto userDto = new UserDto(null, "Test User", "testuser@mail.com");

        UserDto savedUser = userService.createUser(userDto);

        assertNotNull(savedUser.getId());
        assertEquals("Test User", savedUser.getName());

        UserDto retrievedUser = userService.getUserById(savedUser.getId());
        assertEquals(savedUser.getId(), retrievedUser.getId());
        assertEquals(savedUser.getEmail(), retrievedUser.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = new UserDto(null, "Old Name", "old@mail.com");
        UserDto savedUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto(null, "New Name", null); // Обновляем только имя
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertEquals("New Name", updatedUser.getName());
        assertEquals("old@mail.com", updatedUser.getEmail()); // Email должен остаться старым
    }

    @Test
    void shouldThrowNotFoundExceptionForUnknownUser() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }
}
