package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @Autowired
    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<?> addItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                     @RequestBody Item item) {
        return ResponseEntity.ok(itemService.addItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                              @PathVariable int itemId,
                              @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                           @PathVariable int itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<?> getItemsByUser(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return ResponseEntity.ok(itemService.getItemsByUser(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<?> findItemByName(@RequestParam(value = "text", required = false) String text,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return ResponseEntity.ok(itemService.findItemByName(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                 @PathVariable int itemId,
                                 @RequestBody Comment comment) {
        return itemService.addComment(userId, itemId, comment);
    }
}
