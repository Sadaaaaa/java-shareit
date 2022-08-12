package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectItemOwnerException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(User user) {
//        if (!isValid(user)) {
//            throw new IncorrectEmailException("Incorrect user email");
//        }
        User userToDto = userRepository.save(user);
        return UserMapper.toUserDto(userToDto);
    }

    public UserDto updateUser(int userId, User user) {
//        if (!isValid(user)) throw new IncorrectEmailException("User email already exists");
        User userToUpdate = userRepository.findById(userId).get();
        if (user.getName() != null) userToUpdate.setName(user.getName());
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userToUpdate);
    }

    public UserDto getUser(int userId) {
        User userToDto = userRepository.findById(userId).orElseThrow(() -> new IncorrectItemOwnerException("User is not exist"));
        return UserMapper.toUserDto(userToDto);
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    public List<UserDto> getAllUsers() {
       List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    private boolean isValid(User user) {
        boolean isValid = true;

//        if (user.getEmail().isBlank()) {
//            log.warn("User email is empty");
//            isValid = false;
//        }

        if (userRepository.findByEmail(user.getEmail()).size() > 0) {
            log.warn("User email is duplicated");
            isValid = false;
        }

        return isValid;

    }
}
