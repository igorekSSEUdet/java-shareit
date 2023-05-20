package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserDto> createUser(User user);

    Optional<UserDto> getUserById(int id);

    List<User> getAllUsers();

    Optional<UserDto> updateUser(User user, int id);

    int deleteUser(int id);

}
