package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.repository.model.Booking.Status;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.user.dto.ShortUserDto;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BookingDto {
    public static final Status DEFAULT_STATUS = Status.WAITING;

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status = DEFAULT_STATUS;
    private ShortUserDto booker;
    private ShortItemDto item;
}
