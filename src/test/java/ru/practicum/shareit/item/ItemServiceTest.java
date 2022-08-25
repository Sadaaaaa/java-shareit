package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemServiceImpl;
    @Mock
    private BookingServiceImpl bookingService;

    private Item item;
    private Item itemToUpdate;
    private User owner;
    private Booking booking;
    private Comment comment;

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
    void whenCreateItem_thenReturnItemDto() {
        when(itemRepository.save(item)).thenReturn(item);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        assertEquals(itemServiceImpl.addItem(1, item), ItemMapper.toItemDto(item));
        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyInt());
    }

    @Test
    void whenUpdateItem_thenReturnItemDto() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));

        assertEquals(itemServiceImpl.updateItem(1, 1, itemToUpdate), ItemMapper.toItemDto(itemToUpdate));
        verify(itemRepository, Mockito.times(1)).save(item);
        verify(itemRepository, Mockito.times(1)).findById(anyInt());

        assertThrows(BadRequestException.class, () -> itemServiceImpl.updateItem(0, 1, itemToUpdate));
        assertThrows(NotFoundException.class, () -> itemServiceImpl.updateItem(5, 1, itemToUpdate));
    }

    @Test
    void whenGetItem_thenReturnItemDto() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllByItemIdInOrderByStartDesc(anyList())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItem_Id(anyInt())).thenReturn(List.of(comment));

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setNextBooking(BookingMapper.forItem(booking));
        itemDto.setComments(List.of(ItemMapper.toCommentDto(comment)));
        assertEquals(itemServiceImpl.getItem(1, 1), itemDto);

        when(bookingRepository.findAllByItemIdInOrderByStartDesc(anyList())).thenReturn(List.of(booking, booking));
        itemDto.setLastBooking(BookingMapper.forItem(booking));
        assertEquals(itemServiceImpl.getItem(1, 1), itemDto);

        verify(itemRepository, Mockito.times(2)).findById(anyInt());
        verify(bookingRepository, Mockito.times(2)).findAllByItemIdInOrderByStartDesc(anyList());
        verify(commentRepository, Mockito.times(2)).findAllByItem_Id(anyInt());
    }

    @Test
    void whenGetItemsByUser_thenReturnItemDto() {
        when(itemRepository.findAllByOwnerId(anyInt(), any())).thenReturn(List.of(item));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));

        List<ItemDto> itemDtos = Stream.of(item).map(ItemMapper::toItemDto).collect(Collectors.toList());
        itemDtos.get(0).setComments(new ArrayList<>());

        assertEquals(itemServiceImpl.getItemsByUser(1, 0, 5), itemDtos);

        verify(itemRepository, Mockito.times(1)).findById(anyInt());
        verify(itemRepository, Mockito.times(1)).findAllByOwnerId(anyInt(), any());
    }

    @Test
    void whenGetItemsByName_thenReturnItemDto() {
        when(itemRepository.search(anyString(), any())).thenReturn(List.of(item));

        assertEquals(itemServiceImpl.findItemByName("Item", 0, 5), List.of(ItemMapper.toItemDto(item)));
        verify(itemRepository, Mockito.times(1)).search(anyString(), any());

        assertEquals(itemServiceImpl.findItemByName(null, 0, 5), new ArrayList<>());
    }

    @Test
    void whenAddComment_thenReturnCommentDto() {
        when(bookingService.getAllBookings(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(List.of(BookingMapper.toBookingDto(booking)));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(owner));

        assertEquals(itemServiceImpl.addComment(1, 1, comment), ItemMapper.toCommentDto(comment));
        comment.setText("");
        assertThrows(BadRequestException.class, () -> itemServiceImpl.addComment(1, 1, comment));
    }
}
