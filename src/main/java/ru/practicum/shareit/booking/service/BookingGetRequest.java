package ru.practicum.shareit.booking.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingGetRequest {
    private Long userId;
    private String possibleState;
    private Integer from;
    private Integer size;


}
