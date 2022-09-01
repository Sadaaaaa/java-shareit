package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking fromBookingDto(BookingDtoRequest booking) {
        Booking bookingFromDto = new Booking(
                booking.getStart(),
                booking.getEnd(),
                new Item(),
                new User(),
                BookingStatus.WAITING);

        bookingFromDto.getItem().setId(booking.getItemId());
        return bookingFromDto;
    }

    public static BookingDtoForItem forItem(Booking booking) {
        return new BookingDtoForItem(booking.getId(), booking.getBooker().getId());
    }
}
