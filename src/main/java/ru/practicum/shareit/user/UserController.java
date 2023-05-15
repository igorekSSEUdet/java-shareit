package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(@Qualifier("userServiceImpl") UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<UserDto> createUser(@RequestBody @Valid User user) {
        return service.createUser(user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<UserDto>  getUserById(@PathVariable int id) {
        return service.getUserById(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<UserDto> updateUser(@RequestBody User user, @PathVariable int id) {
        return service.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public int deleteUser(@PathVariable int id) {
        return service.deleteUser(id);
    }


}
