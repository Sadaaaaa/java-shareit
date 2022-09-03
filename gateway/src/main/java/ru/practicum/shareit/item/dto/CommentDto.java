package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private int id;
    private String text;
    private ItemDto itemDto;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(int id, String text, ItemDto itemDto, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.itemDto = itemDto;
        this.authorName = authorName;
        this.created = created;
    }
}
