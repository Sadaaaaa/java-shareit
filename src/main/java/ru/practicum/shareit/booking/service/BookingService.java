package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(int userId, BookingDtoRequest booking);

    BookingDto update(int userId, int bookingId, Boolean param);

    BookingDto read(int userId, int bookingId);

    List<BookingDto> getAllBookings(int userId, String state);

    List<BookingDto> getAllBookingsByOwner(int userId, String state);
}
