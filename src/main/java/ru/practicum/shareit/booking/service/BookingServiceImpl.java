package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        if (!itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item is not found!")).getAvailable()) {
            throw new BadRequestException("Item is not available!");
        }

        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User is not found!");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start booking is before current date!");
        }

        if (itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item is not found!")).getOwner().getId() == userId) {
            throw new NotFoundException("You can't book your own item!");
        }

        Booking newBooking = BookingMapper.fromBookingDto(booking);
        newBooking.setItem(itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("User is not found!")));
        newBooking.setBooker(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!")));
        newBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(newBooking);

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto update(int userId, int bookingId, Boolean param) {
        Booking bookingToUpdate = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("User is not found!"));

        if ((bookingToUpdate.getItem().getOwner().getId() == userId) && param) {
            if (bookingToUpdate.getStatus() == BookingStatus.APPROVED) {
                throw new BadRequestException("Booking already approved!");
            }
            bookingToUpdate.setStatus(BookingStatus.APPROVED);
        } else if (bookingToUpdate.getItem().getOwner().getId() == userId && !param) {
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
    public List<BookingDto> getAllBookings(int userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User is not found!");
        }

        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);
        return bookingDtoByState(bookings, state);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(int userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User is not found!");
        }

        List<Integer> itemsIds = itemRepository.findAllByOwnerId(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(itemsIds);

        return bookingDtoByState(bookings, state);
    }

    public List<BookingDto> bookingDtoByState(List<Booking> bookings, String state) {
        switch (state) {
            case "ALL":
                return bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getStart()) && LocalDateTime.now().isBefore(booking.getEnd()))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(booking -> LocalDateTime.now().isBefore(booking.getStart()))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
