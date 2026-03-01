package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

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
        // 1. Подготовка данных (Создаем владельца, вещь и арендатора)
        UserDto owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        UserDto booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));
        ItemDto item = itemService.addItem(owner.getId(), new ItemDto(null, "Drill", "Power Drill", true, null));

        // 2. Создание DTO бронирования
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(null, start, end, item.getId(), null, null, null);

        // 3. Вызов реального метода
        BookingDto savedBooking = bookingService.addBooking(booker.getId(), bookingDto);

        // 4. Проверки
        assertNotNull(savedBooking.getId());
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        assertEquals(item.getId(), savedBooking.getItem().getId());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
    }
}
