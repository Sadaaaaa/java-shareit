package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(int userId, Item item);

    ItemDto updateItem(int userId, int itemId, Item item);

    ItemDto getItem(int itemId);

    List<ItemDto> getItemsByUser(int userId);

    List<ItemDto> findItemByName(String text);
}
