package ru.practicum.shareit.requests.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@Entity
@Table(name = "Requests")
public class ItemRequest {
    @Id
    @Column(name = "requests_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "bookings_description")
    private String description;
    @Column(name = "requestor_id")
    private int requestor;
    private LocalDateTime created;
}
