package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingByBookerIdOrderByStartTimeDesc(
            Long bookerId);

    List<Booking> findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
            Long bookerId, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(
            Long bookerId, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStatusEquals(
            Long bookerId, Booking.Status status);

    List<Booking> findBookingsByItemOwnerIdOrderByStartTimeDesc(
            Long ownerId);

    List<Booking> findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
            Long ownerId, LocalDateTime time);

    List<Booking> findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(
            Long ownerId, LocalDateTime time);

    List<Booking> findBookingsByItemOwnerIdAndStatusEquals(
            Long ownerId, Booking.Status status);

    boolean existsByBookerIdAndItemIdAndEndTimeIsBefore(
            Long bookerId, Long itemId, LocalDateTime time);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIdAndStatus(Long itemId, Booking.Status status);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long ownerId, Booking.Status status);
}
