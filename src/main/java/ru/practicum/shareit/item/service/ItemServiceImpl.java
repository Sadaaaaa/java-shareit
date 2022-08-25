package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository, BookingService bookingService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
    }

    public ItemDto addItem(int userId, Item item) {
        if (userId == 0) throw new BadRequestException("User shouldn't be empty.");
        item.setOwner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!")));

        Item itemToMap = itemRepository.save(item);
        return ItemMapper.toItemDto(itemToMap);
    }

    public ItemDto updateItem(int userId, int itemId, Item item) {
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item is not found!"));

        if (userId == 0) {
            log.warn("Empty header: X-Sharer-User-Id");
            throw new BadRequestException("Bad item header");
        }

        if (itemToUpdate.getOwner().getId() != userId) {
            throw new NotFoundException("Wrong item owner!");
        }

        if (item.getName() != null) itemToUpdate.setName(item.getName());
        if (item.getDescription() != null) itemToUpdate.setDescription(item.getDescription());
        if (item.getAvailable() != null) itemToUpdate.setAvailable(item.getAvailable());

        itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Transactional
    public ItemDto getItem(int itemId, int userId) {
        Item itemToMap = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Wrong item ID!"));
        ItemDto itemDto = ItemMapper.toItemDto(itemToMap);
        List<Booking> bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(List.of(itemId));

        if (itemToMap.getOwner().getId() == userId) {
            if (bookings.size() == 1) {
                itemDto.setNextBooking(BookingMapper.forItem(bookings.get(0)));
            } else if (bookings.size() > 1) {
                itemDto.setNextBooking(BookingMapper.forItem(bookings.get(0)));
                itemDto.setLastBooking(BookingMapper.forItem(bookings.get(1)));
            }
        }
        itemDto.setComments(commentRepository.findAllByItem_Id(itemId).stream().map(ItemMapper::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

    public List<ItemDto> getItemsByUser(int userId, int from, int size) {
        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size));
        List<ItemDto> itemDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        return itemDtos.stream().map((item) -> this.getItem(item.getId(), userId)).sorted(Comparator.comparingInt(ItemDto::getId)).collect(Collectors.toList());
    }

    public List<ItemDto> findItemByName(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.search(text.toLowerCase(), PageRequest.of(from, size));

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public CommentDto addComment(int userId, int itemId, Comment comment) {
        if (comment.getText().isEmpty()) throw new BadRequestException("Comment text is empty.");

        List<BookingDto> bookings = bookingService.getAllBookings(userId, "PAST", 0, 5);
        List<User> bookers = bookings.stream().map(BookingDto::getBooker).collect(Collectors.toList());
        List<Integer> bookersIds = bookers.stream().map(User::getId).collect(Collectors.toList());
        if (bookersIds.size() == 0) throw new BadRequestException("Trying leave comment without booking.");

        comment.setItem(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item is not found!")));
        comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!")));
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return ItemMapper.toCommentDto(comment);
    }
}
