package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.strategy.BookingStrategy;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingStrategy allStrategy;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        // Регистрируем только стратегию ALL, чтобы тесты проверяли логику сервиса, а не выбор стратегий.
        when(allStrategy.getStateName()).thenReturn(BookingState.ALL);
        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository,
                bookingMapper,
                List.of(allStrategy)
        );
        bookingService.init();
    }

    @Test
    void addBooking_ShouldThrowValidationException_WhenItemIsUnavailable() {
        // Нельзя создать бронь на недоступную вещь.
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

    @Test
    void addBooking_ShouldThrowNotFound_WhenOwnerBooksOwnItem() {
        // Владелец не может бронировать свою же вещь.
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                1L, null, null, null);

        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void addBooking_ShouldThrowValidation_WhenDatesInvalid() {
        // Дата начала должна быть строго раньше даты окончания.
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto(null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1),
                1L, null, null, null);

        User booker = new User();
        booker.setId(userId);
        User owner = new User();
        owner.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(userId, bookingDto));
    }

    @Test
    void approveBooking_ShouldThrowNotOwner_WhenOwnerMismatch() {
        // Подтверждать/отклонять бронь может только владелец вещи.
        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStatus(BookingStatus.WAITING);
        Item item = new Item();
        User owner = new User();
        owner.setId(77L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(NotOwnerException.class, () -> bookingService.approveBooking(1L, 10L, true));
    }

    @Test
    void approveBooking_ShouldThrowValidation_WhenStatusAlreadyChanged() {
        // Повторное изменение статуса запрещено.
        Booking booking = new Booking();
        booking.setId(10L);
        booking.setStatus(BookingStatus.APPROVED);
        Item item = new Item();
        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 10L, true));
    }

    @Test
    void getBooking_ShouldThrowNotFound_WhenNoAccess() {
        // Смотреть бронь может только бронирующий или владелец вещи.
        Booking booking = new Booking();
        booking.setId(1L);
        User booker = new User();
        booker.setId(2L);
        booking.setBooker(booker);
        User owner = new User();
        owner.setId(3L);
        Item item = new Item();
        item.setOwner(owner);
        booking.setItem(item);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(5L, 1L));
    }

    @Test
    void getUserBookings_ShouldThrowNotFound_WhenUserMissing() {
        // Для выборки бронирований пользователь должен существовать.
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(1L, "ALL", 0, 10));
    }

    @Test
    void getUserBookings_ShouldUseStrategyAndMapDtos() {
        // Проверяем, что сервис делегирует выборку стратегии и маппит сущности в DTO.
        when(userRepository.existsById(1L)).thenReturn(true);
        Booking booking = new Booking();
        booking.setId(10L);
        BookingDto dto = new BookingDto();
        dto.setId(10L);

        when(allStrategy.findBookings(eq(1L), eq(bookingRepository), any(), eq(false)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(dto);

        List<BookingDto> result = bookingService.getUserBookings(1L, "ALL", 0, 10);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        verify(allStrategy).findBookings(eq(1L), eq(bookingRepository), any(), eq(false));
    }
}
