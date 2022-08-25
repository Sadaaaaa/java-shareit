package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * // TODO .
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Requests")
public class Request {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "description")
    private String description;
    @Column(name = "requestor_id")
    private int requestor;
    @Column(name = "created")
    private LocalDateTime created;
    @OneToMany
    @JoinColumn(name = "request_id")
    @ToString.Exclude
//    @Transient
    private List<Item> items = new ArrayList<>();
}
