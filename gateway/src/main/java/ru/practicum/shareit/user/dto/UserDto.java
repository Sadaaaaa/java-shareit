package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private int id;
    private String name;
    @NotEmpty(message = "Email shouldn't be empty")
    @Email(message = "Email should be valid")
    private String email;
}
