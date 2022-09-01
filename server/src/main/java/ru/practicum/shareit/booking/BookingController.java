package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Map;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                    @RequestBody BookingDtoRequest booking) {
        return bookingService.addNewBooking(userId, booking);
    }

    @GetMapping("{bookingId}")
    public BookingDto readBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                  @PathVariable int bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto updateBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                    @PathVariable int bookingId,
                                    @RequestParam(value = "approved") Boolean isApproved) {
        return bookingService.updateBookingById(userId, bookingId, isApproved);
    }

    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {

        if (size < 1) {
            return new ResponseEntity<>("Page size should not be less than 1.", HttpStatus.BAD_REQUEST);
        } else if (from < 0) {
            return new ResponseEntity<>("Page from should not be less than 0.", HttpStatus.BAD_REQUEST);
        }

        if (state.equals("UNSUPPORTED_STATUS")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"));
        }
        return ResponseEntity.ok(bookingService.getAllBookings(userId, state, from, size));
    }

    @GetMapping("owner")
    public ResponseEntity<?> getAllBookingsByOwner(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                                   @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                   @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                   @RequestParam(value = "size", required = false, defaultValue = "5") int size) {

        if (size < 1) {
            return new ResponseEntity<>("Page size should not be less than 1.", HttpStatus.BAD_REQUEST);
        } else if (from < 0) {
            return new ResponseEntity<>("Page from should not be less than 0.", HttpStatus.BAD_REQUEST);
        }

        if (state.equals("UNSUPPORTED_STATUS")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"));
        }

        return ResponseEntity.ok(bookingService.getAllBookingsByOwner(userId, from, size, state));
    }
}
