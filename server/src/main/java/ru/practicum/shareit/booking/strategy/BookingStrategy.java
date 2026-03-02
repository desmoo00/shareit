package ru.practicum.shareit.booking.strategy;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;

import java.util.List;

public interface BookingStrategy {
    BookingState getStateName();

    List<Booking> findBookings(Long userId, BookingRepository bookingRepository, Pageable pageable, boolean isOwner);
}
