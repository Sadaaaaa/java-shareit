package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated(),
                request.getItems()
        );
    }
}
