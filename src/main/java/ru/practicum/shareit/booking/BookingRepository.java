package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findAllByItem_OwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndEndBefore(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long ownerId, Status status, Sort sort);

    List<Booking> findByItemInAndStatusEquals(List<Item> items, Sort sort, Status status);

    Optional<Booking> findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(
            Long ownerId, Long itemId, LocalDateTime end, Status status);

    Optional<Booking> findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart(
            Long ownerId, Long itemId, LocalDateTime start, Status status);

    List<Booking> findBookingByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime end);
}
