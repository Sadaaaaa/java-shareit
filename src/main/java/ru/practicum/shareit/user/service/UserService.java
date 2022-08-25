package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(User user);

    UserDto updateUser(int userId, User user);

    UserDto getUser(int userId);

    void deleteUser(int userId);

    List<UserDto> getAllUsers();
}
