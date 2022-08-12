package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBooker_IdOrderByStartDesc(int bookerId);
    List<Booking> findAllByItemIdInOrderByStartDesc(List<Integer> itemIds);

}
