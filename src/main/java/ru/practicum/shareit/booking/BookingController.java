package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                    @RequestBody BookingDtoRequest booking) {
        return bookingService.create(userId, booking);
    }

    @GetMapping("{bookingId}")
    public BookingDto readBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                  @PathVariable int bookingId) {
        return bookingService.read(userId, bookingId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto updateBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                    @PathVariable int bookingId,
                                    @RequestParam(value = "approved") Boolean param) {
        return bookingService.update(userId, bookingId, param);
    }

    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {

        if (state.equals("UNSUPPORTED_STATUS")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"));
        }
        return ResponseEntity.ok(bookingService.getAllBookings(userId, state));
    }

    @GetMapping("owner")
    public ResponseEntity<?> getAllBookingsByOwner(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                                  @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"));
        }
        return ResponseEntity.ok(bookingService.getAllBookingsByOwner(userId, state));
    }
}
