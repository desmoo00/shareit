package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Test
    void shouldCreateBooking() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));
        ItemDto item = itemService.addItem(owner.getId(), new ItemDto(null, "Drill", "Power drill", true, null));

        BookingDto bookingDto = new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId(),
                null,
                null,
                null
        );

        BookingDto savedBooking = bookingService.addBooking(booker.getId(), bookingDto);

        assertNotNull(savedBooking.getId());
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        assertEquals(item.getId(), savedBooking.getItem().getId());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
    }

    @Test
    void shouldApproveAndGetBooking() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner2", "owner2@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker2", "booker2@mail.com"));
        ItemDto item = itemService.addItem(owner.getId(), new ItemDto(null, "Saw", "Saw", true, null));

        BookingDto created = bookingService.addBooking(booker.getId(), new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId(),
                null,
                null,
                null
        ));

        BookingDto approved = bookingService.approveBooking(owner.getId(), created.getId(), true);
        BookingDto byBooker = bookingService.getBooking(booker.getId(), created.getId());

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
        assertEquals(created.getId(), byBooker.getId());
    }

    @Test
    void shouldRejectBooking() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner3", "owner3@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker3", "booker3@mail.com"));
        ItemDto item = itemService.addItem(owner.getId(), new ItemDto(null, "Bike", "Bike", true, null));

        BookingDto created = bookingService.addBooking(booker.getId(), new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId(),
                null,
                null,
                null
        ));

        BookingDto rejected = bookingService.approveBooking(owner.getId(), created.getId(), false);
        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }

    @Test
    void shouldReturnUserAndOwnerBookings() {
        UserDto owner = userService.createUser(new UserDto(null, "Owner4", "owner4@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker4", "booker4@mail.com"));
        ItemDto item = itemService.addItem(owner.getId(), new ItemDto(null, "Laptop", "Laptop", true, null));

        BookingDto created = bookingService.addBooking(booker.getId(), new BookingDto(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId(),
                null,
                null,
                null
        ));
        bookingService.approveBooking(owner.getId(), created.getId(), true);

        List<BookingDto> userBookings = bookingService.getUserBookings(booker.getId(), "ALL", 0, 10);
        List<BookingDto> ownerBookings = bookingService.getOwnerBookings(owner.getId(), "ALL", 0, 10);

        assertEquals(1, userBookings.size());
        assertEquals(1, ownerBookings.size());
    }

    @Test
    void shouldThrowValidationExceptionForUnknownState() {
        UserDto user = userService.createUser(new UserDto(null, "User", "state@mail.com"));
        assertThrows(ValidationException.class, () -> bookingService.getUserBookings(user.getId(), "UNKNOWN", 0, 10));
    }
}
