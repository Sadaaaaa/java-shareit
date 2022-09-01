package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class BookingDtoForItem {
    private int id;
    private int bookerId;

    public BookingDtoForItem(int id, int bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
