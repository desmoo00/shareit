package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toBookingDto_ShouldReturnNull_WhenBookingNull() {
        // MapStruct должен корректно обрабатывать null-вход.
        assertNull(mapper.toBookingDto(null));
    }

    @Test
    void toBooking_ShouldHandlePartialInput() {
        // Проверяем дефолтный статус WAITING и перенос полей при частичном входе.
        Item item = new Item();
        item.setId(5L);
        User booker = new User();
        booker.setId(7L);

        Booking booking = mapper.toBooking(null, item, booker);

        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertNull(booking.getStart());
        assertNull(booking.getEnd());
    }

    @Test
    void toItemDto_ShouldMapRequestId() {
        // requestId должен прокидываться из item.request.id.
        ItemRequest request = new ItemRequest();
        request.setId(11L);
        Item item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        item.setRequest(request);

        assertEquals(11L, mapper.toItemDto(item).getRequestId());
    }

    @Test
    void toBookingDto_ShouldMapNestedFields() {
        // Проверка маппинга вложенных объектов booker/item в DTO.
        User booker = new User();
        booker.setId(2L);
        booker.setName("booker");
        booker.setEmail("b@mail.com");
        Item item = new Item();
        item.setId(4L);
        item.setName("item");
        item.setDescription("desc");
        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        BookingDto dto = mapper.toBookingDto(booking);

        assertEquals(1L, dto.getId());
        assertEquals(4L, dto.getItemId());
        assertEquals(2L, dto.getBooker().getId());
    }
}
