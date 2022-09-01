package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.Item;

import java.util.Map;

public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<?> addItem(int userId, Item item) {
        return post("", userId, item);
    }

    public ResponseEntity<?> updateItem(int userId, int itemId, Item item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<?> getItem(int itemId, int userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<?> getItemsByUser(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", userId, parameters);
    }

    public ResponseEntity<?> findItemByName(String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/search?text=" + text, parameters);
    }

    public ResponseEntity<?> addComment(Long userId, Integer itemId, Comment comment) {
        return post("/" + itemId + "/comment", comment);
    }
}
