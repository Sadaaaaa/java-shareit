package ru.practicum.shareit.user.dao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto addUser(User user);
    User getUser(int id);
    User updateUser(int userId, User user);
    void deleteUser(int id);
    List<UserDto> getAllUsers();

}
