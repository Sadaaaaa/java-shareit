package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.Item;
import java.time.LocalDateTime;
import java.util.List;

/**
 * // TODO .
 */
@Data
public class RequestDto {
    private int id;
    private String description;
    private int requestor;
    private LocalDateTime created;
    private List<Item> items;

    public RequestDto(Integer id, String description, Integer requestor, LocalDateTime created, List<Item> items) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
        this.items = items;
    }
}
