package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.exception.IncorrectItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemStorageInMemory implements ItemStorage {
    Map<Integer, Item> items = new HashMap<>();
    UserStorage userStorage;
    int i = 0;

    @Autowired
    public ItemStorageInMemory(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public ItemDto addItem(int userId, Item item) {
        i++;
        item.setId(i);
        item.setOwner(userStorage.getUser(userId));
        items.put(i, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, Item item) {
        if ((items.get(itemId).getOwner() != null) && (items.get(itemId).getOwner().getId() != userId)) {
            log.warn("Incorrect owner: Wrong item updated owner");
            throw new IncorrectItemOwnerException("Wrong item updated owner");
        }

        Item updItem = items.values()
                .stream()
                .filter(i -> i.getId() == itemId)
                .findAny()
                .orElseThrow();

        updItem.setId(itemId);
        if (item.getName() != null) updItem.setName(item.getName());
        if (item.getDescription() != null) updItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)  updItem.setAvailable(item.getAvailable());
        updItem.setOwner(userStorage.getUser(userId));
//        updItem.setRequest();

        for(Item x : items.values()) {
            if (x.getId() == itemId) {
                items.remove(itemId);
                items.put(itemId, updItem);
            }
        }

        return ItemMapper.toItemDto(updItem);
    }

    @Override
    public ItemDto getItem(int itemId) {
        Item item = items.values()
                .stream()
                .filter(i -> i.getId() == itemId)
                .findAny()
                .get();
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUser(int userId) {
        List<ItemDto> itemsDtoByUser = new ArrayList<>();
        List<Item> filteredByUser = items.values().stream().filter(u -> u.getOwner().getId() == userId).collect(Collectors.toList());
        for(Item x : filteredByUser) {
            itemsDtoByUser.add(ItemMapper.toItemDto(x));
        }
        return itemsDtoByUser;
    }

    @Override
    public List<ItemDto> findItemByName(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemsDtoByName = new ArrayList<>();

        List<Item> filteredByName = items.values()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        for(Item x : filteredByName) {
            itemsDtoByName.add(ItemMapper.toItemDto(x));
        }
        return itemsDtoByName;
    }

}
