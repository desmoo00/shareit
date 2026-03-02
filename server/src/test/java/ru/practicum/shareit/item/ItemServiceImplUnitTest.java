package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingMapper bookingMapper;

    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        // Тестируем сервис изолированно от БД через моки репозиториев.
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository,
                bookingMapper
        );
    }

    @Test
    void addItem_ShouldThrowNotFound_WhenUserMissing() {
        // Добавлять вещь может только существующий пользователь.
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addItem(1L, new ItemDto(null, "name", "desc", true, null)));
    }

    @Test
    void addItem_ShouldThrowNotFound_WhenRequestMissing() {
        // Если requestId передан, заявка должна существовать.
        User owner = new User();
        owner.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addItem(1L, new ItemDto(null, "name", "desc", true, 99L)));
    }

    @Test
    void addItem_ShouldSave_WhenRequestExists() {
        // Успешный сценарий: вещь сохраняется с привязкой к заявке.
        User owner = new User();
        owner.setId(1L);
        ItemRequest request = new ItemRequest();
        request.setId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.addItem(1L, new ItemDto(null, "name", "desc", true, 10L));

        assertEquals("name", result.getName());
        assertEquals(10L, result.getRequestId());
    }

    @Test
    void updateItem_ShouldThrowNotFound_WhenItemMissing() {
        // Нельзя обновить несуществующую вещь.
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, new ItemDto(null, "new", "new", true, null)));
    }

    @Test
    void updateItem_ShouldThrowNotFound_WhenNotOwner() {
        // Обновлять вещь может только ее владелец.
        User owner = new User();
        owner.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, new ItemDto(null, "new", "new", true, null)));
    }

    @Test
    void addComment_ShouldThrowNotFound_WhenUserMissing() {
        // Комментарий может оставить только существующий пользователь.
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 2L, new CommentDto(null, "text", null, LocalDateTime.now())));
    }

    @Test
    void addComment_ShouldThrowNotFound_WhenItemMissing() {
        // Комментарий нельзя добавить к несуществующей вещи.
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(1L, 2L, new CommentDto(null, "text", null, LocalDateTime.now())));
    }

    @Test
    void addComment_ShouldThrowValidation_WhenNoCompletedBooking() {
        // Комментарий разрешен только после завершенной APPROVED брони.
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(bookingRepository.hasCompletedBooking(anyLong(), anyLong(), any(), any(BookingStatus.class)))
                .thenReturn(false);

        assertThrows(ValidationException.class,
                () -> itemService.addComment(1L, 2L, new CommentDto(null, "text", null, LocalDateTime.now())));
    }
}
