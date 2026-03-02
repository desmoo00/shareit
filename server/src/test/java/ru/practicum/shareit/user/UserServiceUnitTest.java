package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void updateUser_ShouldThrowNotFound_WhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(1L, new UserDto(null, "n", "e@mail.com")));
    }

    @Test
    void getUserById_ShouldThrowNotFound_WhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_ShouldUpdateOnlyProvidedFields() {
        User user = new User();
        user.setId(1L);
        user.setName("old");
        user.setEmail("old@mail.com");
        UserDto dto = new UserDto(1L, "old", "old@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toUserDto(any(User.class))).thenReturn(dto);

        UserDto result = userService.updateUser(1L, new UserDto(null, null, "new@mail.com"));

        assertEquals(1L, result.getId());
        assertEquals("new@mail.com", user.getEmail());
        assertEquals("old", user.getName());
    }
}
