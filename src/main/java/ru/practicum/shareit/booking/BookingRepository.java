package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enumeration.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime dateTime, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long booker_id, Status status);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime localDateTime);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime localDateTime);

    List<Booking> findAllByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    Optional<Booking> findFirstByItem_Owner_IdAndItem_IdOrderByStart(Long ownerId, Long itemId);

    Optional<Booking> findFirstByItem_OwnerIdAndIdOrderByStartDesc(Long ownerId, Long itemId);

    List<Booking> findBookingByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime localDateTime);


}
