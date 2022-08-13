package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@Entity
@Table(name = "Bookings")
public class Booking {
    @Id
    @Column(name = "bookings_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "bookings_start_date")
    private LocalDateTime start;
    @Column(name = "bookings_end_date")
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "bookings_item_id", referencedColumnName = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "user_id")
    private User booker;
    @Column(name = "bookings_status")
    @Enumerated(EnumType.ORDINAL)
    private BookingStatus status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }

    public Booking() {
    }
}
