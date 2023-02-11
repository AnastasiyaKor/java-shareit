package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStatusException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.Status.REJECTED;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Booking createRequest(BookingRequestDto bookingDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя бронировать свой товар");
        }
        if (!item.getAvailable()) {
            throw new ValidatorException("Неверые данные");
        }
        if (!item.getId().equals(bookingDto.getItemId())) {
            throw new ValidatorException("Неверный идентификатор товара");
        }
        Booking booking = BookingMapper.fromBookingRequestDto(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    @Transactional
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
        return booking;
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
    public List<Booking> getAllBookingUser(long userId, BookingState status, int from, int size) {
        LocalDateTime date = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, sort);
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        switch (status) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId, pageable).getContent();
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                        userId, date, date, pageable).getContent();
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBefore(userId, date, pageable).getContent();
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfter(userId, date, pageable).getContent();
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatus(userId, WAITING, pageable).getContent();
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(userId, REJECTED, pageable).getContent();
            default:
                throw new UnknownStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<Booking> getAllItemsBookingUser(long ownerId, BookingState status, int from, int size) {
        LocalDateTime date = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from, size, sort);
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        switch (status) {
            case ALL:
                return bookingRepository.findAllByItem_OwnerId(ownerId, pageable);
            case CURRENT:
                return bookingRepository.findAllByItem_OwnerIdAndStartBeforeAndEndAfter(
                        ownerId, date, date, pageable);
            case PAST:
                return bookingRepository.findAllByItem_OwnerIdAndEndBefore(ownerId, date, pageable);
            case FUTURE:
                return bookingRepository.findAllByItem_OwnerIdAndStartAfter(ownerId, date, pageable);
            case WAITING:
                return bookingRepository.findAllByItem_OwnerIdAndStatus(ownerId, WAITING, pageable);
            case REJECTED:
                return bookingRepository.findAllByItem_OwnerIdAndStatus(ownerId, REJECTED, pageable);
            default:
                throw new UnknownStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
