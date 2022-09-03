package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String STATUS = "UNSUPPORTED_STATUS";

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        BookingState state;
        try {
            state = BookingState.from(stateParam).orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown state: " + stateParam));
        }

        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {

        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<?> updateBooking(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) int userId,
                                           @PathVariable int bookingId,
                                           @RequestParam(value = "approved") Boolean isApproved) {
        return bookingClient.updateBookingById(userId, bookingId, isApproved);
    }

    @GetMapping("owner")
    public ResponseEntity<?> getAllBookingsByOwner(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0", required = false) Long userId,
                                                   @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                   @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                   @RequestParam(value = "size", required = false, defaultValue = "5") int size) {

        if (size < 1) {
            return new ResponseEntity<>("Page size should not be less than 1.", HttpStatus.BAD_REQUEST);
        } else if (from < 0) {
            return new ResponseEntity<>("Page from should not be less than 0.", HttpStatus.BAD_REQUEST);
        }

        if (state.equals(STATUS)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unknown state: " + STATUS));
        }

        return bookingClient.getAllBookingsByOwner(userId, from, size, state);
    }


}
