package ru.practicum.shareit.booking.exception;

import org.apache.commons.lang3.NotImplementedException;
import ru.practicum.shareit.booking.service.BookingService;

import static java.lang.String.format;

public class StateNotImplementedException extends NotImplementedException {
    private static final String STATE_NOT_IMPLEMENTED = "State '%s' not implemented";

    public StateNotImplementedException(String message) {
        super(message);
    }

    public static StateNotImplementedException getFromState(BookingService.State state) {
        return new StateNotImplementedException(format(STATE_NOT_IMPLEMENTED, state));
    }
}
