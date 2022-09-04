package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private int id;
    private String name;
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private Integer requestId;

}
