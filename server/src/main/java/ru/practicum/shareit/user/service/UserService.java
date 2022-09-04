package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(int userId, UserDto userDto);

    UserDto getUser(int userId);

    void deleteUser(int userId);

    List<UserDto> getAllUsers();
}
