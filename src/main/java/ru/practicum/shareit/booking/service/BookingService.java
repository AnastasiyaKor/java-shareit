package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    Booking createRequest(BookingDto bookingDto, Long userId);

    Booking changeStatusRequest(Long userId, Boolean approved, Long bookingId);

    Booking getId(Long userId, long bookingId);

    List<Booking> getAllBookingUser(long userId, String status);

    List<Booking> getAllItemsBookingUser(long userId, String status);

}
