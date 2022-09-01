package ru.practicum.shareit.booking.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDtoRequest {
    private int itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}

