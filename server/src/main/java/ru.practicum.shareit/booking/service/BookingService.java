package ru.practicum.shareit.booking.service;



import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    Booking createRequest(BookingRequestDto bookingDto, Long userId);

    Booking changeStatusRequest(Long userId, Boolean approved, Long bookingId);

    Booking getId(Long userId, long bookingId);

    List<Booking> getAllBookingUser(long userId, BookingState status, int from, int size);

    List<Booking> getAllItemsBookingUser(long userId, BookingState status, int from, int size);

}
