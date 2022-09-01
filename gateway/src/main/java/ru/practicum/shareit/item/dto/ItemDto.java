package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

/**
 * // TODO .
 */
@Data
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;

    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;


    public ItemDto(int id, String name, String description, Boolean available, Integer requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }


}
