package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * // TODO .
 */
@Data
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
