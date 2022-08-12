package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.IncorrectItemOwnerException;
import ru.practicum.shareit.exception.IncorrectItemRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private BookingService bookingService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository, BookingService bookingService) {

        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
    }

    public ItemDto addItem(int userId, Item item) {
        item.setOwner(userRepository.findById(userId).get());
        Item itemToMap = itemRepository.save(item);
        return ItemMapper.toItemDto(itemToMap);
    }

    public ItemDto updateItem(int userId, int itemId, Item item) {
        if (userId == 0) {
            log.warn("Empty header: X-Sharer-User-Id");
            throw new IncorrectItemRequestException("Bad item header");
        }

        if (itemRepository.findById(itemId).get().getOwner().getId() != userId) {
            throw new IncorrectItemOwnerException("Wrong item owner!");
        }

        Item itemToUpdate = itemRepository.findById(itemId).get();
        if (item.getName() != null) itemToUpdate.setName(item.getName());
        if (item.getDescription() != null) itemToUpdate.setDescription(item.getDescription());
        if (item.getAvailable() != null) itemToUpdate.setAvailable(item.getAvailable());

        itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(itemToUpdate);
    }

    @Transactional
    public ItemDto getItem(int itemId, int userId) {
        Item itemToMap = itemRepository.findById(itemId).orElseThrow(() -> new IncorrectItemOwnerException("Wrong item ID!"));
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

//        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
//        if (comments.size() == 0) {
//            itemDto.setComments(new CommentDto());
//        }
        itemDto.setComments(commentRepository.findAllByItem_Id(itemId).stream()
                .map(ItemMapper::toCommentDto).collect(Collectors.toList()));

        return itemDto;
    }

    public List<ItemDto> getItemsByUser(int userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtos = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        List<ItemDto> itemDtos1 = itemDtos.stream()
                .map((item) -> this.getItem(item.getId(), userId))
                .sorted(Comparator.comparingInt(ItemDto::getId))
                .collect(Collectors.toList());
        return itemDtos1;
    }

    @Transactional
    public List<ItemDto> findItemByName(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.search(text.toLowerCase());


        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public void isValidItem(Item item) {
        if (item.getAvailable() == null) {
            log.warn("Bad item status: Item is not available");
            throw new IncorrectItemRequestException("Item is not available");
        }

        if (item.getName().isBlank()) {
            log.warn("Bad user name: User name is empty");
            throw new IncorrectItemRequestException("User name is empty");
        }

        if (item.getDescription() == null) {
            log.warn("Bad item description: Item description is empty");
            throw new IncorrectItemRequestException("Item description is empty");
        }
    }

    public void isValidUserId(int userId) {
        if (userId == 0) {
            log.warn("Empty header: X-Sharer-User-Id");
            throw new IncorrectItemRequestException("Bad item header");
        }
    }

    @Override
    public CommentDto addComment(int userId, int itemId, Comment comment) {

        if (comment.getText().isEmpty()) {
            throw new IncorrectItemRequestException("Comment text is empty.");
        }

        List<BookingDto> bookings = bookingService.getAllBookings(userId, "PAST");
        List<User> bookers = bookings.stream().map(BookingDto::getBooker).collect(Collectors.toList());
        List<Integer> bookersIds = bookers.stream().map(User::getId).collect(Collectors.toList());
        if (bookersIds.size() == 0) {
            throw new IncorrectItemRequestException("Trying leave comment without booking.");
        }


        comment.setItem(itemRepository.findById(itemId).orElseThrow());
        comment.setAuthor(userRepository.findById(userId).orElseThrow());
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return ItemMapper.toCommentDto(comment);
    }
}
