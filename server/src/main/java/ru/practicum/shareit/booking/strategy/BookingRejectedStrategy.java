package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingRejectedStrategy implements BookingStrategy {
    @Override
    public BookingState getStateName() {
        return BookingState.REJECTED;
    }

    @Override
    public List<Booking> findBookings(Long userId, BookingRepository bookingRepository, Pageable pageable, boolean isOwner) {
        if (isOwner) {
            return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
        } else {
            return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
        }
    }
}
