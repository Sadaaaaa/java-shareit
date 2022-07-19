package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(int userId, User user) {
        return userStorage.updateUser(userId, user);
    }

    public User getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public List<User> getAllUsers() {
       return userStorage.getAllUsers();
    }
}
