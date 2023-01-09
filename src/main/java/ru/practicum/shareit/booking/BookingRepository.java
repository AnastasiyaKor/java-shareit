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

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findByItemIn(List<Item> items, Sort sort);

    Optional<Booking> findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(
            Long ownerId, Long itemId, LocalDateTime end, Status status);

    Optional<Booking> findFirstByItem_OwnerIdAndIdAndStartAfterOrderByStart(
            Long ownerId, Long itemId, LocalDateTime start);

    List<Booking> findBookingByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime end);
}
