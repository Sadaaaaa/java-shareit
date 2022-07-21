package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * // TODO .
 */
@Data
public class User {
    private int id;
    private String name;
    @NotEmpty
    @Email
    private String email;
}
