package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "status", constant = "WAITING")
    Booking toBooking(BookingDto bookingDto, Item item, User booker);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toItemDto(Item item);
}
