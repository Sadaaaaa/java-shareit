package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDto {
    private int id;
    private String description;
    private int requestor;
    private LocalDateTime created;
    @ToString.Exclude
    private List<ItemDto> itemDtos = new ArrayList<>();
}
