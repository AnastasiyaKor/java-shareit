package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItem_OwnerId(Long ownerId, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndEndBefore(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long ownerId, Status status, Pageable pageable);

    List<Booking> findByItemInAndStatusEquals(List<Item> items, Sort sort, Status status);

    Optional<Booking> findFirstByItem_Owner_IdAndItem_IdAndEndLessThanEqualAndStatusEqualsOrderByStartDesc(
            Long ownerId, Long itemId, LocalDateTime end, Status status);

    Optional<Booking> findFirstByItem_OwnerIdAndIdAndStartAfterAndStatusEqualsOrderByStart(
            Long ownerId, Long itemId, LocalDateTime start, Status status);

    List<Booking> findBookingByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime end);
}
