package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;

import java.util.Comparator;
import java.util.List;

public final class ItemRequestMapper {
    private ItemRequestMapper() {
    }

    public static ItemRequestDto toDto(ItemRequest request, List<Item> items) {
        List<ItemRequestItemDto> itemDtos = items == null ? List.of() : items.stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemRequestMapper::toItemDto)
                .toList();

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos
        );
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                null
        );
    }

    public static ItemRequestItemDto toItemDto(Item item) {
        return new ItemRequestItemDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
