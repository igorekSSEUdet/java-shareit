package ru.practicum.shareit.request.dto;


import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestDto {

    private String description;
    private User requestor;
    private LocalDateTime created;
}
