package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.IncorrectItemOwnerException;
import ru.practicum.shareit.exception.IncorrectItemRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto create(int userId, BookingDtoRequest booking) {
        if (!itemRepository.findById(booking.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item is not found!")).getAvailable()) {
            throw new IncorrectItemRequestException("Item is not available!");
        }

        if (userRepository.findById(userId).isEmpty()) {
            throw new ItemNotFoundException("User is not found!");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectItemRequestException("Start booking is before current date!");
        }

        if (itemRepository.findById(booking.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item is not found!")).getOwner().getId() == userId) {
            throw new ItemNotFoundException("You can't book your own item!");
        }

        Booking newBooking = BookingMapper.fromBookingDto(booking);
        newBooking.setItem(itemRepository.findById(booking.getItemId()).orElseThrow());
        newBooking.setBooker(userRepository.findById(userId).orElseThrow(() -> new ItemNotFoundException("User is not found!")));
        newBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(newBooking);

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto update(int userId, int bookingId, Boolean param) {
        Booking bookingToUpdate = bookingRepository.findById(bookingId).orElseThrow();


        if ((bookingToUpdate.getItem().getOwner().getId() == userId) && param) {
            if (bookingToUpdate.getStatus() == BookingStatus.APPROVED) {
                throw new IncorrectItemRequestException("Booking already approved!");
            }
            bookingToUpdate.setStatus(BookingStatus.APPROVED);
        } else if (bookingToUpdate.getItem().getOwner().getId() == userId && !param) {
            bookingToUpdate.setStatus(BookingStatus.REJECTED);
        } else {
            throw new IncorrectItemOwnerException("Wrong item owner!");
        }

        bookingRepository.save(bookingToUpdate);

        return BookingMapper.toBookingDto(bookingToUpdate);
    }

    @Override
    public BookingDto read(int userId, int bookingId) {

//        if (userRepository.findById(userId).isEmpty()) {
//            throw new ItemNotFoundException("User is not found!");
//        }


        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ItemNotFoundException("Wrong booking ID!"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ItemNotFoundException("Wrong user request!");
        }
    }

    @Override
    public List<BookingDto> getAllBookings(int userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ItemNotFoundException("User is not found!");
        }

        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);

        switch (state) {
            case "ALL":
                return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
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
                throw new IncorrectItemRequestException("Unknown state: UNSUPPORTED_STATUS");
        }


//        List<Booking> bookings = bookingRepository.findAll();
//        List<BookingDto> bookingDtos = bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
//        Collections.reverse(bookingDtos);


//        return null;
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(int userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ItemNotFoundException("User is not found!");
        }

        List<Integer> itemsIds = itemRepository.findAllByOwnerId(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(itemsIds);

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
                throw new IncorrectItemRequestException("Unknown state: UNSUPPORTED_STATUS");
        }


//        List<Integer> itemsIds = itemRepository.findAllByOwnerId(userId).stream()
//                .map(Item::getId)
//                .collect(Collectors.toList());
//        List<Booking> bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(itemsIds);
//
//        return bookings.stream()
//                .map(BookingMapper::toBookingDto)
//                .collect(Collectors.toList());
    }


}
