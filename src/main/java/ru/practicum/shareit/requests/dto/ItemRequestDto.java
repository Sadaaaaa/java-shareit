package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
public class ItemRequestDto {
    private String description;
    private User requestor;
    private LocalDateTime created;

    public ItemRequestDto(String description, User requestor, LocalDateTime created) {
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
