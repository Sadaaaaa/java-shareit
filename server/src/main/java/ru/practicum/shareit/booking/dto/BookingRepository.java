package ru.practicum.shareit.booking.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerId(Integer userId, Pageable pageable);

    List<Booking> findAllByItemIdInOrderByStartDesc(List<Integer> itemIds);

    List<Booking> findAllByItemOwner(User user, Pageable pageable);


}
