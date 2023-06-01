package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.StateNotImplementedException;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static ru.practicum.shareit.booking.service.BookingService.*;
import static ru.practicum.shareit.item.service.ItemService.checkItemExistsById;
import static ru.practicum.shareit.item.service.ItemService.checkOwnerOfItemByItemIdAndUserId;
import static ru.practicum.shareit.user.service.UserService.checkUserExistsById;

@Service
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoMapper bookingDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final ItemDtoMapper itemDtoMapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              BookingDtoMapper bookingDtoMapper,
                              UserDtoMapper userDtoMapper,
                              ItemDtoMapper itemDtoMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingDtoMapper = bookingDtoMapper;
        this.userDtoMapper = userDtoMapper;
        this.itemDtoMapper = itemDtoMapper;
    }

    @Override
    public BookingDto addBooking(BookingCreationRequestDto bookingDto, Long userId) {
        checkUserExistsById(userRepository, userId);
        checkItemExistsById(itemRepository, bookingDto.getItemId());
        checkUserNotOwnerByItemIdAndUserId(itemRepository, bookingDto.getItemId(), userId);
        checkBookingTimePeriod(bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = bookingDtoMapper.toBooking(bookingDto, userId);
        checkItemAvailableForBooking(booking.getItem());

        Booking savedBooking = bookingRepository.save(booking);
        log.debug("Booking ID_{} added.", savedBooking.getId());

        return bookingDtoMapper.toBookingDto(savedBooking, userDtoMapper, itemDtoMapper);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long userId) {
        checkBookingExistsById(bookingRepository, bookingId);
        checkUserExistsById(userRepository, userId);

        Booking booking = bookingRepository.findById(bookingId).get();
        checkOwnerOfItemByItemIdAndUserId(itemRepository, booking.getItem().getId(), userId);
        checkBookingStatusNotApprove(booking);

        booking.setStatus((approved == Boolean.TRUE) ? (Status.APPROVED) : (Status.REJECTED));

        log.debug("Booking ID_{} updated.", bookingId);
        Booking updatedBooking = bookingRepository.save(booking);

        return bookingDtoMapper.toBookingDto(updatedBooking, userDtoMapper, itemDtoMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long bookingId, Long userId) {
        checkBookingExistsById(bookingRepository, bookingId);
        checkUserExistsById(userRepository, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow();//fixme
        checkOwnerOrBooker(booking, userId);

        log.debug("Booking ID_{} returned.", booking.getId());
        return bookingDtoMapper.toBookingDto(booking, userDtoMapper, itemDtoMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBookerId(Long bookerId, String possibleState) {
        checkUserExistsById(userRepository, bookerId);
        State state = checkState(possibleState);
        switch (state) {
            case ALL:
                return bookingDtoMapper.toBookingDto(
                        getAllBookingsByBookerId(bookerId),
                        userDtoMapper,
                        itemDtoMapper);
            case CURRENT:
                return bookingDtoMapper.toBookingDto(
                        getCurrentBookingsByBookerId(bookerId),
                        userDtoMapper,
                        itemDtoMapper);
            case PAST:
                return bookingDtoMapper.toBookingDto(
                        getPastBookingsByBookerId(bookerId),
                        userDtoMapper,
                        itemDtoMapper);
            case FUTURE:
                return bookingDtoMapper.toBookingDto(
                        getFutureBookingsByBookerId(bookerId),
                        userDtoMapper,
                        itemDtoMapper);
            case WAITING:
                return bookingDtoMapper.toBookingDto(
                        getWaitingBookingsByBookerId(bookerId),
                        userDtoMapper,
                        itemDtoMapper);
            case REJECTED:
                return bookingDtoMapper.toBookingDto(
                        getRejectedBookingsByBookerId(bookerId),
                        userDtoMapper,
                        itemDtoMapper);
            default:
                throw StateNotImplementedException.getFromState(state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBookerItems(Long ownerId, String possibleState) {
        checkUserExistsById(userRepository, ownerId);
        State state = checkState(possibleState);
        switch (state) {
            case ALL:
                return bookingDtoMapper.toBookingDto(
                        getAllBookingsByOwnerId(ownerId),
                        userDtoMapper,
                        itemDtoMapper);
            case CURRENT:
                return bookingDtoMapper.toBookingDto(
                        getCurrentBookingsByOwnerId(ownerId),
                        userDtoMapper,
                        itemDtoMapper);
            case PAST:
                return bookingDtoMapper.toBookingDto(//fixme
                        getPastBookingsByOwnerId(ownerId),
                        userDtoMapper,
                        itemDtoMapper);
            case FUTURE:
                return bookingDtoMapper.toBookingDto(
                        getFutureBookingsByOwnerId(ownerId),
                        userDtoMapper,
                        itemDtoMapper);
            case WAITING:
                return bookingDtoMapper.toBookingDto(
                        getWaitingBookingsByOwnerId(ownerId),
                        userDtoMapper,
                        itemDtoMapper);
            case REJECTED:
                return bookingDtoMapper.toBookingDto(
                        getRejectedBookingsByOwnerId(ownerId),
                        userDtoMapper,
                        itemDtoMapper);
            default:
                throw StateNotImplementedException.getFromState(state);
        }
    }

    private List<Booking> getAllBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(bookerId);
    }

    private List<Booking> getCurrentBookingsByBookerId(Long bookerId) {
        LocalDateTime time = now();
        return bookingRepository.findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(bookerId, time, time);
    }

    private List<Booking> getPastBookingsByBookerId(Long bookerId) {
        return bookingRepository.findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(bookerId, now());
    }

    private List<Booking> getFutureBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(bookerId, now());
    }

    private List<Booking> getWaitingBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, Status.WAITING);
    }

    private List<Booking> getRejectedBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, Status.REJECTED);
    }

    private List<Booking> getAllBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeDesc(ownerId);
    }

    private List<Booking> getCurrentBookingsByOwnerId(Long ownerId) {
        LocalDateTime time = LocalDateTime.now();
        return bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(ownerId, time, time);
    }

    private List<Booking> getPastBookingsByOwnerId(Long ownerId) {

//        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(ownerId);
//        return bookings.stream().filter(b -> now().isAfter(b.getEndTime())).collect(Collectors.toList());

        return bookingRepository.findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(ownerId, now());
    }

    private List<Booking> getFutureBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(ownerId, now());
    }

    private List<Booking> getWaitingBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.WAITING);
    }

    private List<Booking> getRejectedBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.REJECTED);
    }
}
