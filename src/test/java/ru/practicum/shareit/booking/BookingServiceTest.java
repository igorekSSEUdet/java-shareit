package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingLogicException;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingGetRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.Booking.Status.*;
import static ru.practicum.shareit.booking.model.Booking.builder;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingDtoMapper bookingDtoMapper;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private ItemDtoMapper itemDtoMapper;
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        this.bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingDtoMapper, userDtoMapper, itemDtoMapper);
    }


    @Test
    public void testAddBooking_Success() {
        BookingCreationRequestDto bookingDto = new BookingCreationRequestDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(bookingDto.getItemId());
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.existsById(item.getId())).thenReturn(true);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(Item.builder().owner(User.builder().id(2L).build()).build());
        when(bookingDtoMapper.toBooking(any(BookingCreationRequestDto.class), eq(user.getId()))).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingDtoMapper.toBookingDto(any(Booking.class), eq(userDtoMapper), eq(itemDtoMapper))).thenReturn(new BookingDto());

        BookingDto result = bookingService.addBooking(bookingDto, user.getId());

        assertNotNull(result);
        verify(userRepository).existsById(user.getId());
        verify(itemRepository).existsById(item.getId());
        verify(bookingDtoMapper).toBooking(any(BookingCreationRequestDto.class), eq(user.getId()));
        verify(bookingRepository).save(booking);
        verify(bookingDtoMapper).toBookingDto(any(Booking.class), eq(userDtoMapper), eq(itemDtoMapper));
    }

    @Test()
    public void testAddBooking_OwnerIdEqualsBookerId() {
        BookingCreationRequestDto bookingDto = new BookingCreationRequestDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(bookingDto.getItemId());
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.existsById(item.getId())).thenReturn(true);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        assertThrows(BookingLogicException.class, () ->
                bookingService.addBooking(bookingDto, user.getId()));

    }

    @Test
    public void testUpdateBookingStatus() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        User user = User.builder().id(1L).build();

        Item item = Item.builder().id(1L).owner(user).build();

        Booking booking = new Booking();
        booking.setStatus(WAITING);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStartTime(LocalDateTime.now().plusDays(2));
        booking.setEndTime(LocalDateTime.now().plusDays(3));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.getReferenceById(item.getId())).thenReturn(item);

        when(bookingDtoMapper.toBookingDto(booking, userDtoMapper, itemDtoMapper))
                .thenReturn(BookingDto.builder()
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build());

        BookingDto bookingDto = bookingService.updateBookingStatus(1L, true, 1L);
        System.out.println(bookingDto);
        assertEquals(APPROVED, booking.getStatus());
    }

    @Test
    public void testGetBooking() {

        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingDtoMapper.toBookingDto(booking, userDtoMapper, itemDtoMapper))
                .thenReturn(BookingDto.builder().id(booking.getId()).build());

        BookingDto bookingDto = bookingService.getBooking(1L, 1L);

        assertEquals(1L, bookingDto.getId());
    }


    @Test
    public void testGetAllBookingsByBookerId() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(anyLong()))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(null)
                .size(null).build();
        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaiting() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(WAITING)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(null)
                .size(null).build();
        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejected() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(REJECTED)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdWithPagination() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(anyLong(),
                eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaitingWithPagination() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(WAITING), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejectedWithPagination() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(REJECTED), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdForOwner() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeDesc(anyLong()))
                .thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(booking.getId()).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaitingForOwner() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(WAITING)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejectedForOwner() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(REJECTED)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(null)
                .size(null).build();
        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdWithPaginationForOwner() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeAsc(anyLong(),
                eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaitingWithPaginationForOwner() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(WAITING), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejectedWithPaginationForOwner() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(REJECTED), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }


}




