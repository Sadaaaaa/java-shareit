package ru.practicum.shareit.user.dao;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IncorrectEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
@Repository
public class UserStorageInMemory implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    public List<UserDto> getAllUsers() {
        List<UserDto> usersDto = new ArrayList<>();
        for (User x : users.values()) {
            usersDto.add(UserMapper.toUserDto(x));
        }
        return usersDto;
    }

    @Override
    public UserDto addUser(User user) {
        if (!isValid(user)) throw new IncorrectEmailException("Incorrect user email");

        id++;
        user.setId(id);
        users.put(id, user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }

    @Override
    public User updateUser(int userId, User user) {
        if (!isValid(user)) throw new IncorrectEmailException("Incorrect user email");

        User updateUser = users.get(userId);
        if (user.getName() != null) updateUser.setName(user.getName());
        if (user.getEmail() != null) updateUser.setEmail(user.getEmail());
        users.put(userId, updateUser);
        return updateUser;
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }

    private boolean isValid(User user) {
        boolean isValid = true;

        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.warn("User email is duplicated");
            isValid = false;
        }

        return isValid;

    }
}
