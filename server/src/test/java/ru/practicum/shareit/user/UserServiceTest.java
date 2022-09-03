package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    User user = User.builder()
            .id(1)
            .name("User")
            .email("user@test.com")
            .build();

    @Test
    void whenCreateUser_thenReturnUserDto() {
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        Assertions.assertEquals(userServiceImpl.createUser(user), UserMapper.toUserDto(user));
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void whenUpdateUser_thenReturnUserDto() {
        User userToUpdate = User.builder()
                .id(1)
                .name("userToUpdate")
                .email("userToUpdate@test.com")
                .build();

        Mockito.when(userRepository.findById(1)).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Assertions.assertEquals(userServiceImpl.updateUser(1, userToUpdate), UserMapper.toUserDto(userToUpdate));
        Mockito.verify(userRepository, Mockito.times(1)).findById(1);
    }

    @Test
    void whenGetUser_thenReturnUserDto() {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.ofNullable(user));
        Assertions.assertEquals(userServiceImpl.getUser(1), UserMapper.toUserDto(user));
        Mockito.verify(userRepository, Mockito.times(1)).findById(1);
    }

    @Test
    void whenDeleteUser() {
        userServiceImpl.deleteUser(1);
//        userRepository.deleteById(1);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    void whenGetAllUser_thenReturnUserDto() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        Assertions.assertEquals(userServiceImpl.getAllUsers(), Stream.of(user).map(UserMapper::toUserDto).collect(Collectors.toList()));
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }
}
