package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemWithBookingDto extends ItemDto {
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    public ItemWithBookingDto(Long id, String name, String description, Boolean available, Long requestId,
                              BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        super(id, name, description, available, requestId);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}
