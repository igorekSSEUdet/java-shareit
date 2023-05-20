package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final DateTimeFormatter logTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private int id = 0;

    @Override
    public Optional<UserDto> createUser(User user) {
        if (isEmailExistsWithoutId(user.getEmail())) {
            throw new EmailExistException("User with" + user.getEmail() + "email already exists");
        } else {
            user.setId(++id);
            users.put(user.getId(), user);
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Создан новый пользователь " + user);
            return Optional.of(UserDtoMapper.toUserDto(user));
        }
    }

    @Override
    public Optional<UserDto> getUserById(int userId) {
        if (users.containsKey(userId)) {
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Получен пользователь с id = " + userId);
            return Optional.of(UserDtoMapper.toUserDto(users.get(userId)));
        } else {
            throw new UserNotFoundException("User with userId = " + userId + " not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        log.info(LocalDateTime.now().format(logTimeFormat) + " : Получен список всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<UserDto> updateUser(User user, int id) {
        User user1 = getUser(id);
        if (checkHasUser(id) && !isEmailExists(user.getEmail(), id)) {
            users.put(id, getUserForUpdate(user1, user));
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Обновлен пользователь с id = " + id);
            return Optional.of(UserDtoMapper.toUserDto(users.get(id)));
        } else throw new EmailExistException("User with" + user.getEmail() + "email already exists");
    }

    @Override
    public int deleteUser(int userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            log.info(LocalDateTime.now().format(logTimeFormat) + " : Удален пользователь с userId = " + userId);
            return userId;
        } else throw new RuntimeException();
    }

    @Override
    public boolean checkHasUser(int userId) {
        return users.containsKey(userId);
    }

    private boolean isEmailExists(String email, int userId) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email) && u.getId() != userId);
    }

    private boolean isEmailExistsWithoutId(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    private User getUser(int id) {
        if (users.containsKey(id)) return users.get(id);
        else throw new RuntimeException();
    }

    private User getUserForUpdate(User userDto, User user) {
        if (user.getEmail() != null) {
            userDto.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userDto.setName(user.getName());
        }
        if (user.getId() != null) {
            userDto.setId(user.getId());
        }
        return userDto;
    }

}
