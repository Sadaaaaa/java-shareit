package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    @Mock
    private RequestServiceImpl requestService;
    @InjectMocks
    private RequestController controller;
    @Autowired
    MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    private User user;
    private UserDto userDto;
    private Request request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        userDto = new UserDto(
                1,
                "John",
                "john.doe@mail.com");

        user = new User(
                1,
                "John",
                "john.doe@mail.com");

        request = Request.builder()
                .id(1)
                .description("New description")
                .requestor(1)
                .build();
    }

    @Test
    void add_requestTest() throws Exception {
        Mockito.when(requestService.addRequest(anyInt(), any())).thenReturn(RequestMapper.toRequestDto(request));
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId())))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requestor", is(request.getRequestor())));
    }

    @Test
    void get_requestTest() throws Exception {
        Mockito.when(requestService.getRequest(anyInt())).thenReturn(List.of(RequestMapper.toRequestDto(request)));
        mockMvc.perform(get("/requests")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(request.getId())))
                .andExpect(jsonPath("$.[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(request.getRequestor())));
    }

    @Test
    void get_allRequestsTest() throws Exception {
        Mockito.when(requestService.getAllRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(RequestMapper.toRequestDto(request)));
        mockMvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(request))
                        .param("from", "0")
                        .param("size", "7")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(request.getId())))
                .andExpect(jsonPath("$.[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(request.getRequestor())));
    }

    @Test
    void get_requestByIdTest() throws Exception {
        Mockito.when(requestService.getRequestById(anyInt(), anyInt())).thenReturn(RequestMapper.toRequestDto(request));
        mockMvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId())))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requestor", is(request.getRequestor())));
    }
}
