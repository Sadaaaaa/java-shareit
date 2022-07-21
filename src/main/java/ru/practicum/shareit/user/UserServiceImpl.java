package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(User user) {
        return userStorage.addUser(user);
    }

    public UserDto updateUser(int userId, User user) {
        return UserMapper.toUserDto(userStorage.updateUser(userId, user));
    }

    public UserDto getUser(int userId) {

        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public List<UserDto> getAllUsers() {
       return userStorage.getAllUsers();
    }
}
