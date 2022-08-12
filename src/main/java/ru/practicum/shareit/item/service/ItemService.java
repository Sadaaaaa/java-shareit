package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(int userId, Item item);

    ItemDto updateItem(int userId, int itemId, Item item);

    ItemDto getItem(int itemId, int userId);

    List<ItemDto> getItemsByUser(int userId);

    List<ItemDto> findItemByName(String text);

    CommentDto addComment(int userId, int itemId, Comment comment);
}
