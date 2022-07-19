package ru.practicum.shareit.user.dao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);
    User getUser(int id);
    User updateUser(int userId, User user);
    public void deleteUser(int id);
    List<User> getAllUsers();

}
