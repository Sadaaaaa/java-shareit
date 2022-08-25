package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(int userId, BookingDtoRequest booking);

    BookingDto update(int userId, int bookingId, Boolean isApproved);

    BookingDto read(int userId, int bookingId);

    List<BookingDto> getAllBookings(int userId, String state, int from, int size);

    List<BookingDto> getAllBookingsByOwner(int userId, int from, int size, String state);
}
