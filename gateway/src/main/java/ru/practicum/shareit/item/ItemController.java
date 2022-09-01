package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.Item;
/**
 * // TODO .
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<?> addItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                     @RequestBody Item item) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getDescription() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return itemClient.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                              @PathVariable int itemId,
                              @RequestBody Item item) {
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItem(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                           @PathVariable int itemId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<?> getItemsByUser(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) Long userId,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {

        if (size < 1) {
            return new ResponseEntity<>("Page size should not be less than 1.", HttpStatus.BAD_REQUEST);
        } else if (from < 0) {
            return new ResponseEntity<>("Page from should not be less than 0.", HttpStatus.BAD_REQUEST);
        }

        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<?> findItemByName(@RequestParam(value = "text", required = false) String text,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                            @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {

        if (size < 1) {
            return new ResponseEntity<>("Page size should not be less than 1.", HttpStatus.BAD_REQUEST);
        } else if (from < 0) {
            return new ResponseEntity<>("Page from should not be less than 0.", HttpStatus.BAD_REQUEST);
        }

        return itemClient.findItemByName(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<?> addComment(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) Long userId,
                                 @PathVariable Integer itemId,
                                 @RequestBody Comment comment) {
        return itemClient.addComment(userId, itemId, comment);
    }
}
