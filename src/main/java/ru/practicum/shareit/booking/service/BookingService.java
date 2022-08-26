package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(int userId, BookingDtoRequest booking);

    BookingDto updateBookingById(int userId, int bookingId, Boolean isApproved);

    BookingDto findBookingById(int userId, int bookingId);

    List<BookingDto> getAllBookings(int userId, String state, int from, int size);

    List<BookingDto> getAllBookingsByOwner(int userId, int from, int size, String state);
}
