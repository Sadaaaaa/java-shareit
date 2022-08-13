package ru.practicum.shareit.requests.dto;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
public class ItemRequestDto {
    private String description;
    private int requestor;
    private LocalDateTime created;

    public ItemRequestDto(String description, Integer requestor, LocalDateTime created) {
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
