package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.OffsetLimitPageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto create(int userId, BookingDtoRequest booking) {
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item is not found!"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available!");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start booking is before current date!");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("You can't book your own item!");
        }

        Booking newBooking = BookingMapper.fromBookingDto(booking);
        newBooking.setItem(item);
        newBooking.setBooker(user);
        newBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(newBooking);

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto update(int userId, int bookingId, Boolean isApproved) {
        Booking bookingToUpdate = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("User is not found!"));

        if ((bookingToUpdate.getItem().getOwner().getId() == userId) && isApproved) {
            if (bookingToUpdate.getStatus() == BookingStatus.APPROVED) {
                throw new BadRequestException("Booking already approved!");
            }
            bookingToUpdate.setStatus(BookingStatus.APPROVED);
        } else if (bookingToUpdate.getItem().getOwner().getId() == userId && !isApproved) {
            bookingToUpdate.setStatus(BookingStatus.REJECTED);
        } else {
            throw new NotFoundException("Wrong item owner!");
        }

        bookingRepository.save(bookingToUpdate);

        return BookingMapper.toBookingDto(bookingToUpdate);
    }

    @Override
    public BookingDto read(int userId, int bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Wrong booking ID!"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Wrong user request!");
        }
    }

    @Override
    public List<BookingDto> getAllBookings(int userId, String state, int from, int size) {
        if (userRepository.findById(userId) == null || userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User is not found!");
        }

        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings = bookingRepository.findAllByBookerId(userId, pageable);
        return BookingsByState.bookingDtoByState(bookings, state);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(int userId, int from, int size, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User is not found!");
        }

        List<Integer> itemsIds = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size)).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(itemsIds);
        return BookingsByState.bookingDtoByState(bookings, state);
    }
}
