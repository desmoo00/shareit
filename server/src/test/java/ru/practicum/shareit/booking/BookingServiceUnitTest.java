package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addBooking_ShouldThrowValidationException_WhenItemIsUnavailable() {
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L, null, null, null);

        User booker = new User();
        booker.setId(userId);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(false); // Устанавливаем статус "недоступно"

        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // Проверяем, что метод выбрасывает нужное исключение
        assertThrows(ValidationException.class, () -> bookingService.addBooking(userId, bookingDto));
    }
}
