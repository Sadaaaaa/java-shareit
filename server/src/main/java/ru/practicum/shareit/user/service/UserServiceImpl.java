package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(User user) {
        User userToDto = userRepository.save(user);
        return UserMapper.toUserDto(userToDto);
    }

    public UserDto updateUser(int userId, User user) {
        User userToUpdate = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found!"));
        if (user.getName() != null) userToUpdate.setName(user.getName());
        if (user.getEmail() != null) userToUpdate.setEmail(user.getEmail());

        userRepository.save(userToUpdate);

        return UserMapper.toUserDto(userToUpdate);
    }

    public UserDto getUser(int userId) {
        User userToDto = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not exist"));
        return UserMapper.toUserDto(userToDto);
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
