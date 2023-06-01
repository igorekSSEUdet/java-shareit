package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utills.UserHttpHeaders;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                 @RequestBody @Valid BookingCreationRequestDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingService.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByBookerId(@RequestHeader(UserHttpHeaders.USER_ID) Long bookerId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByBookerItems(@RequestHeader(UserHttpHeaders.USER_ID) Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllByBookerItems(ownerId, state);
    }
}
