package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingsByState {

    public static List<BookingDto> bookingDtoByState(List<Booking> bookings, String state) {
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
