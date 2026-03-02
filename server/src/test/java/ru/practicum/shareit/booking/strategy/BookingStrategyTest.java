package ru.practicum.shareit.booking.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingStrategyTest {

    @Mock
    private BookingRepository bookingRepository;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final Long userId = 1L;

    @Test
    void bookingAllStrategy_ShouldCallCorrectMethods() {
        BookingAllStrategy strategy = new BookingAllStrategy();

        // Проверяем, что стратегия возвращает правильное имя состояния
        assertEquals(BookingState.ALL, strategy.getStateName());

        // Проверяем вызов для владельца (isOwner = true)
        strategy.findBookings(userId, bookingRepository, pageable, true);
        verify(bookingRepository).findAllByItemOwnerId(userId, pageable);

        // Проверяем вызов для арендатора (isOwner = false)
        strategy.findBookings(userId, bookingRepository, pageable, false);
        verify(bookingRepository).findAllByBookerId(userId, pageable);
    }

    @Test
    void bookingCurrentStrategy_ShouldCallCorrectMethods() {
        BookingCurrentStrategy strategy = new BookingCurrentStrategy();
        assertEquals(BookingState.CURRENT, strategy.getStateName());

        // Для isOwner = true
        strategy.findBookings(userId, bookingRepository, pageable, true);
        // Поскольку LocalDateTime.now() вычисляется внутри метода, используем any(LocalDateTime.class)
        verify(bookingRepository).findAllByItemOwnerIdAndCurrent(eq(userId), any(LocalDateTime.class), eq(pageable));

        // Для isOwner = false
        strategy.findBookings(userId, bookingRepository, pageable, false);
        verify(bookingRepository).findAllByBookerIdAndCurrent(eq(userId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void bookingFutureStrategy_ShouldCallCorrectMethods() {
        BookingFutureStrategy strategy = new BookingFutureStrategy();
        assertEquals(BookingState.FUTURE, strategy.getStateName());

        // Для isOwner = true
        strategy.findBookings(userId, bookingRepository, pageable, true);
        verify(bookingRepository).findAllByItemOwnerIdAndStartAfter(eq(userId), any(LocalDateTime.class), eq(pageable));

        // Для isOwner = false
        strategy.findBookings(userId, bookingRepository, pageable, false);
        verify(bookingRepository).findAllByBookerIdAndStartAfter(eq(userId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void bookingPastStrategy_ShouldCallCorrectMethods() {
        BookingPastStrategy strategy = new BookingPastStrategy();
        assertEquals(BookingState.PAST, strategy.getStateName());

        // Для isOwner = true
        strategy.findBookings(userId, bookingRepository, pageable, true);
        verify(bookingRepository).findAllByItemOwnerIdAndEndBefore(eq(userId), any(LocalDateTime.class), eq(pageable));

        // Для isOwner = false
        strategy.findBookings(userId, bookingRepository, pageable, false);
        verify(bookingRepository).findAllByBookerIdAndEndBefore(eq(userId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void bookingWaitingStrategy_ShouldCallCorrectMethods() {
        BookingWaitingStrategy strategy = new BookingWaitingStrategy();
        assertEquals(BookingState.WAITING, strategy.getStateName());

        // Для isOwner = true
        strategy.findBookings(userId, bookingRepository, pageable, true);
        verify(bookingRepository).findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);

        // Для isOwner = false
        strategy.findBookings(userId, bookingRepository, pageable, false);
        verify(bookingRepository).findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
    }

    @Test
    void bookingRejectedStrategy_ShouldCallCorrectMethods() {
        BookingRejectedStrategy strategy = new BookingRejectedStrategy();
        assertEquals(BookingState.REJECTED, strategy.getStateName());

        // Для isOwner = true
        strategy.findBookings(userId, bookingRepository, pageable, true);
        verify(bookingRepository).findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);

        // Для isOwner = false
        strategy.findBookings(userId, bookingRepository, pageable, false);
        verify(bookingRepository).findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
    }
}
