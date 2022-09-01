package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private Item item;
    private Item itemToUpdate;
    private User owner;
    private Booking booking;
    private Comment comment;
    private BookingDtoRequest bookingDtoRequest;
    private Request request;

    @BeforeEach
    void setUp() {
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
                .id(0)
                .item(item)
                .booker(owner)
                .start(LocalDateTime.now())
                .status(BookingStatus.WAITING)
                .build();

        comment = Comment.builder()
                .id(1)
                .item(item)
                .author(owner)
                .text("Nice item")
                .build();

        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .build();

        request = Request.builder()
                .id(1)
                .description("New description")
                .requestor(1)
                .created(LocalDateTime.now())
                .build();

    }

    @Test
    void whenAddRequest_thenReturnRequestDto() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        assertEquals(requestService.addRequest(1, request), RequestMapper.toRequestDto(request));

        request.setDescription(null);
        assertThrows(BadRequestException.class, () -> requestService.addRequest(1, request));

        when(userRepository.findById(anyInt())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> requestService.addRequest(1, request));
    }

    @Test
    void whenGetRequest_thenReturnRequestDtos() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(requestRepository.findAllByRequestor(anyInt())).thenReturn(List.of(request));
        assertEquals(requestService.getRequest(1), List.of(RequestMapper.toRequestDto(request)));

        when(userRepository.findById(anyInt())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> requestService.getRequest(1));
    }

    @Test
    void whenGetAllRequests_thenReturnRequestDtos() {
        when(requestRepository.search(anyInt(), any())).thenReturn(List.of(request));
        assertEquals(requestService.getAllRequests(1, 0, 5), List.of(RequestMapper.toRequestDto(request)));
    }

    @Test
    void whenGetRequestById_thenReturnRequestDto() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(request));
        assertEquals(requestService.getRequestById(1, 1), RequestMapper.toRequestDto(request));

        when(userRepository.findById(anyInt())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1, 1));
    }

}
