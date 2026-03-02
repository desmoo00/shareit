package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingPastStrategy implements BookingStrategy {
    @Override
    public BookingState getStateName() {
        return BookingState.PAST;
    }

    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository, Pageable pageable, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();
        if (isOwner) {
            return bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now, pageable);
        } else {
            return bookingRepository.findAllByBookerIdAndEndBefore(userId, now, pageable);
        }
    }
}
