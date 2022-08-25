package ru.practicum.shareit.booking.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

//    List<Booking> findByBookerIdOrderByStartDesc(int bookerId, Pageable pageable);
    List<Booking> findAllByBookerId(Integer userId, Pageable pageable);

    List<Booking> findAllByItemIdInOrderByStartDesc(List<Integer> itemIds);
}
