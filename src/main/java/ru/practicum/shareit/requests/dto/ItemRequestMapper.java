package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated()
        );
    }
}
