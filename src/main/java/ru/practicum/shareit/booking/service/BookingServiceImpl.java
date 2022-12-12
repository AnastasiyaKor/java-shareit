package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enumeration.BookingState;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStatusException;
import ru.practicum.shareit.exceptions.ValidatorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.enumeration.Status.REJECTED;
import static ru.practicum.shareit.booking.enumeration.Status.WAITING;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Booking createRequest(Booking booking, Long userId) {
        final LocalDateTime date = LocalDateTime.now();
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя бронировать свой товар");
        }
        if (item.getId().equals(booking.getItem().getId())) {
            if (booking.getEnd().isAfter(date)
                    && booking.getEnd().isAfter(booking.getStart())
                    && booking.getStart().isAfter(date) && item.getAvailable()) {
                booking.setStatus(WAITING);
                bookingRepository.save(booking);
            } else {
                throw new ValidatorException("Неверые данные");
            }
        } else {
            throw new ValidatorException("Неверный идентификатор товара");
        }
        return booking;
    }

    @Override
    public Booking changeStatusRequest(Long userId, Boolean approved, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("бронирование не найдено"));
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidatorException("Бронирование уже одобрено");
        }
        if (booking.getItem() != null && booking.getItem().getOwner().getId().equals(userId)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(REJECTED);
            }
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getId(Long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingUser(long userId, BookingState state) {
        LocalDateTime date = LocalDateTime.now();
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, date, date);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, date);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, date);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, REJECTED);
            default:
                throw new UnknownStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<Booking> getAllItemsBookingUser(long ownerId, BookingState state) {
        LocalDateTime date = LocalDateTime.now();
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        switch (state) {
            case ALL:
                return bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId);
            case CURRENT:
                return bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, date, date);
            case PAST:
                return bookingRepository.findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(ownerId, date);
            case FUTURE:
                return bookingRepository.findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(ownerId, date);
            case WAITING:
                return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, WAITING);
            case REJECTED:
                return bookingRepository.findAllByItem_OwnerIdAndStatusOrderByStartDesc(ownerId, REJECTED);
            default:
                throw new UnknownStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
