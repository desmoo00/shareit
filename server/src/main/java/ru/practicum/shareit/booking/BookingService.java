package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getOwnerBookings(Long userId, String state, Integer from, Integer size);
}
