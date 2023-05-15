package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;

@RestControllerAdvice(assignableTypes = {ItemController.class , UserController.class})
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> UserNotFoundException(final UserNotFoundException e) {
        return Map.of("User exception: ", e.getMessage());
    }

    @ExceptionHandler({EmailExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> EmailExistException(final EmailExistException e) {
        return Map.of("User exception: ", e.getMessage());
    }
}
