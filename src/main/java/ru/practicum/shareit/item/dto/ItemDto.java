package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * // TODO .
 */
@Data
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private Integer integer;

    public ItemDto(int id, String name, String description, boolean available, Integer integer) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.integer = integer;
    }
}
