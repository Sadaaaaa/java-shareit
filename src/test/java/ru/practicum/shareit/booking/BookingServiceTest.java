package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Item item;
    private Item itemToUpdate;
    private User owner;
    private Booking booking;
    private Comment comment;
    private BookingDtoRequest bookingDtoRequest;

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

    }

    @Test
    void whenCreateBooking_thenReturnBookingDto() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));

        bookingDtoRequest.setStart(LocalDateTime.of(2022, 12, 12, 0,0,0,0));
        booking.setStart(LocalDateTime.of(2022, 12, 12, 0,0,0,0));

        assertEquals(bookingService.create(2, bookingDtoRequest), BookingMapper.toBookingDto(booking));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyInt());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyInt());

        assertThrows(NotFoundException.class, () -> bookingService.create(1, bookingDtoRequest));

        bookingDtoRequest.setStart(LocalDateTime.of(2020, 12, 13, 0,0,0,0));
        assertThrows(BadRequestException.class, () -> bookingService.create(2, bookingDtoRequest));
    }

    @Test
    void updateCreateBooking_thenReturnBookingDto() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));

        assertEquals(bookingService.update(1, 1, true), BookingMapper.toBookingDto(booking));
        assertEquals(bookingService.update(1, 1, false), BookingMapper.toBookingDto(booking));

        assertThrows(NotFoundException.class, () -> bookingService.update(2, 1, true));

        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(BadRequestException.class, () -> bookingService.update(1, 1, true));
    }

    @Test
    void updateReadBooking_thenReturnBookingDto() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));

        assertEquals(bookingService.read(1, 1), BookingMapper.toBookingDto(booking));
        assertThrows(NotFoundException.class, () -> bookingService.read(2, 2));
    }

    @Test
    void getAllBookings_thenReturnBookingDtos() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findAllByBookerId(anyInt(), any())).thenReturn(List.of(booking));
        assertEquals(bookingService.getAllBookings(1, "WAITING", 0, 5), List.of(BookingMapper.toBookingDto(booking)));

        when(userRepository.findById(anyInt())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> bookingService.getAllBookings(1, "WAITING", 0, 5));
    }

    @Test
    void getAllBookingsByOwner_thenReturnBookingDtos() {
        booking.setStatus(BookingStatus.PAST);
        booking.setEnd(booking.getStart().minusDays(1));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwner(any(), any())).thenReturn(List.of(booking));

        assertEquals(bookingService.getAllBookingsByOwner(1, 0, 5, "PAST"), List.of(BookingMapper.toBookingDto(booking)));
    }
}
