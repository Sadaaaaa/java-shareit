package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingServiceImpl bookingService;
    @InjectMocks
    private BookingController controller;
    @Autowired
    MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private BookingDtoRequest bookingDtoRequest;

    private Item item;
    private Item itemToUpdate;
    private User owner;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1)
                .build();


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

        itemToUpdate = Item.builder()
                .id(1)
                .name("Item update")
                .description("Item update description")
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1)
                .item(item)
                .booker(owner)
                .build();

        comment = Comment.builder()
                .id(1)
                .item(item)
                .author(owner)
                .text("Nice item")
                .build();
    }

    @Test
    void create_BookingTest() throws Exception {
        Mockito.when(bookingService.addNewBooking(anyInt(), any())).thenReturn(BookingMapper.toBookingDto(booking));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", owner.getId())
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.start", is(booking.getStart())))
                .andExpect(jsonPath("$.end", is(booking.getEnd())));
    }

    @Test
    void read_BookingTest() throws Exception {
        Mockito.when(bookingService.findBookingById(anyInt(), anyInt())).thenReturn(BookingMapper.toBookingDto(booking));
        mockMvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.start", is(booking.getStart())))
                .andExpect(jsonPath("$.end", is(booking.getEnd())));
    }

    @Test
    void update_BookingTest() throws Exception {
        Mockito.when(bookingService.updateBookingById(anyInt(), anyInt(), any())).thenReturn(BookingMapper.toBookingDto(booking));
        mockMvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.start", is(booking.getStart())))
                .andExpect(jsonPath("$.end", is(booking.getEnd())));
    }

    @Test
    void get_allBookingsTest() throws Exception {
        Mockito.when(bookingService.getAllBookings(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(BookingMapper.toBookingDto(booking)));
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "7")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId())))
                .andExpect(jsonPath("$.[0].start", is(booking.getStart())))
                .andExpect(jsonPath("$.[0].end", is(booking.getEnd())));

//        mockMvc.perform(get("/bookings")
//                        .param("state", "ALL")
//                        .param("from", "0")
//                        .param("size", "0")
//                        .content(mapper.writeValueAsString(bookingDtoRequest))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isBadRequest());

//        mockMvc.perform(get("/bookings")
//                        .param("state", "ALL")
//                        .param("from", "-1")
//                        .param("size", "7")
//                        .content(mapper.writeValueAsString(bookingDtoRequest))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isBadRequest());

//        mockMvc.perform(get("/bookings")
//                        .param("state", "UNSUPPORTED_STATUS")
//                        .param("from", "0")
//                        .param("size", "7")
//                        .content(mapper.writeValueAsString(bookingDtoRequest))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isBadRequest());
    }

    @Test
    void get_allBookingsByOwner() throws Exception {
        Mockito.when(bookingService.getAllBookingsByOwner(anyInt(), anyInt(), anyInt(), anyString())).thenReturn(List.of(BookingMapper.toBookingDto(booking)));
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "7")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId())))
                .andExpect(jsonPath("$.[0].start", is(booking.getStart())))
                .andExpect(jsonPath("$.[0].end", is(booking.getEnd())));

//        mockMvc.perform(get("/bookings/owner")
//                        .param("state", "ALL")
//                        .param("from", "0")
//                        .param("size", "0")
//                        .content(mapper.writeValueAsString(bookingDtoRequest))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isBadRequest());

//        mockMvc.perform(get("/bookings/owner")
//                        .param("state", "ALL")
//                        .param("from", "-1")
//                        .param("size", "7")
//                        .content(mapper.writeValueAsString(bookingDtoRequest))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isBadRequest());

//        mockMvc.perform(get("/bookings/owner")
//                        .param("state", "UNSUPPORTED_STATUS")
//                        .param("from", "0")
//                        .param("size", "7")
//                        .content(mapper.writeValueAsString(bookingDtoRequest))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isBadRequest());
    }
}
