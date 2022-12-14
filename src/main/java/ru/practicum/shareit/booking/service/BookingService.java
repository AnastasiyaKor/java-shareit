package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enumeration.BookingState;

import java.util.List;

public interface BookingService {
    Booking createRequest(BookingRequestDto bookingDto, Long userId);

    Booking changeStatusRequest(Long userId, Boolean approved, Long bookingId);

    Booking getId(Long userId, long bookingId);

    List<Booking> getAllBookingUser(long userId, BookingState status);

    List<Booking> getAllItemsBookingUser(long userId, BookingState status);

}
