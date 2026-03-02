package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingAllStrategy implements BookingStrategy {
    @Override
    public BookingState getStateName() {
        return BookingState.ALL;
    }

    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository, Pageable pageable, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findAllByItemOwnerId(userId, pageable);
        } else {
            return bookingRepository.findAllByBookerId(userId, pageable);
        }
    }
}
