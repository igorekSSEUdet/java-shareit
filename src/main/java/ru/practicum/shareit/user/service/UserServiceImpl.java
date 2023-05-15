package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Optional<UserDto> createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public Optional<UserDto> getUserById(int id) {
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public Optional<UserDto> updateUser(User user, int id) {
        return userStorage.updateUser(user, id);
    }

    @Override
    public int deleteUser(int id) {
        return userStorage.deleteUser(id);
    }
}
