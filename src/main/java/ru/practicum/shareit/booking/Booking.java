package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * // TODO .
 */
@Data
public class Booking {
    private int id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
