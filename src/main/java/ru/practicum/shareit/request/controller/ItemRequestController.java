package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.utills.UserHttpHeaders;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody @Valid RequestDto requestDto,
                                        @RequestHeader(UserHttpHeaders.USER_ID) Long userId) {

        return service.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllOwnRequests(@RequestHeader(UserHttpHeaders.USER_ID) Long userId) {
        return service.getAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                         @PathVariable Long requestId) {
        return service.getRequestById(requestId, userId);
    }
}
