package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController controller;

    @Autowired
    MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private Item item;
    private User owner;
    private Comment comment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        owner = new User(
                1,
                "John",
                "john.doe@mail.com");

        item = Item.builder()
                .id(1)
                .name("Item")
                .description("Item description")
                .available(true)
                .owner(owner)
                .build();

        comment = Comment.builder()
                .id(1)
                .item(item)
                .author(owner)
                .text("Nice item")
                .build();
    }

    @Test
    void post_addItemTest() throws Exception {
        Mockito.when(itemService.addItem(anyInt(), any())).thenReturn(ItemMapper.toItemDto(item));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));

        item.setAvailable(null);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patch_updateItemTest() throws Exception {
        Mockito.when(itemService.updateItem(anyInt(), anyInt(), any())).thenReturn(ItemMapper.toItemDto(item));
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void get_itemByIdTest() throws Exception {
        Mockito.when(itemService.getItem(anyInt(), anyInt())).thenReturn(ItemMapper.toItemDto(item));
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void get_itemsByUserTest() throws Exception {
        Mockito.when(itemService.getItemsByUser(anyInt(), anyInt(), anyInt())).thenReturn(List.of(ItemMapper.toItemDto(item)));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())));
    }

    @Test
    void find_itemByNameTest() throws Exception {
        Mockito.when(itemService.findItemByName(anyString(), anyInt(), anyInt())).thenReturn(List.of(ItemMapper.toItemDto(item)));
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "7")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "0")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("text", "item")
                        .param("from", "-1")
                        .param("size", "7")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_addCommentTest() throws Exception {
        Mockito.when(itemService.addComment(anyInt(), anyInt(), any())).thenReturn(ItemMapper.toCommentDto(comment));
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Nice item")));
    }
}
