package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectItemOwnerException;
import ru.practicum.shareit.exception.IncorrectItemRequestException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private ItemStorage itemStorage;
    private UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public ItemDto addItem(int userId, Item item) {

        isValidItem(item);
        isValidUserId(userId);

        item.setId(userId);
        return itemStorage.addItem(userId, item);
    }

    public ItemDto updateItem(int userId, int itemId, Item item) {
        if(userId == 0) {
            log.warn("Empty header: X-Sharer-User-Id");
            throw new IncorrectItemRequestException("Bad item header");
        }
        return itemStorage.updateItem(userId, itemId, item);
    }

    public ItemDto getItem(int itemId) {
        return itemStorage.getItem(itemId);
    }

    public List<ItemDto> getItemsByUser(int userId) {
        return itemStorage.getItemsByUser(userId);
    }

    public List<ItemDto> findItemByName(String text) {
        return itemStorage.findItemByName(text);
    }

    public void isValidItem(Item item) {
        if (item.getAvailable() == null) {
            log.warn("Bad item status: Item is not available");
            throw new IncorrectItemRequestException("Item is not available");
        }

        if (item.getName().isBlank()) {
            log.warn("Bad user name: User name is empty");
            throw new IncorrectItemRequestException("User name is empty");
        }

        if (item.getDescription() == null) {
            log.warn("Bad item description: Item description is empty");
            throw new IncorrectItemRequestException("Item description is empty");
        }
    }

    public void isValidUserId(int userId) {

        if(userId == 0) {
            log.warn("Empty header: X-Sharer-User-Id");
            throw new IncorrectItemRequestException("Bad item header");
        }

        if(userStorage.getAllUsers().stream().noneMatch(i -> i.getId() == userId)) {
            log.warn("Bad item owner: not found");
            throw new IncorrectItemOwnerException("Item owner not found");
        }
    }
}
