package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enumeration.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createRequest(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                    @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос на добавление бронирования от пользователя" + userId);

        Booking booking = bookingService.createRequest(bookingRequestDto, userId);
        return BookingMapper.toBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatusRequest(@RequestHeader("X-Sharer-User-id") Long userId,
                                          @RequestParam Boolean approved, @PathVariable Long bookingId) {
        log.info("Получен запрос на обновление статуса бронирования");
        Booking booking = bookingService.changeStatusRequest(userId, approved, bookingId);
        return BookingMapper.toBookingDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getId(@RequestHeader("X-Sharer-User-id") long userId,
                            @PathVariable long bookingId) {
        log.info("Получен запрос на просмотр бронирования");
        return BookingMapper.toBookingDto(bookingService.getId(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingUser(@RequestHeader("X-Sharer-User-id") long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                              @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("Получен запрос на просмотр списка всех бронирований пользователя");

        List<Booking> bookings = bookingService.getAllBookingUser(userId, state, from, size);
        return BookingMapper.toListBookingDto(bookings);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsBookingUser(@RequestHeader("X-Sharer-User-id") long userId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestParam(required = false, defaultValue = "0")
                                                   @Min(0) int from,
                                                   @RequestParam(required = false, defaultValue = "10")
                                                   @Min(1) int size) {
        log.info("Получен запрос на просмотр списка бронирований для всех вещей пользователя");
        List<Booking> bookings = bookingService.getAllItemsBookingUser(userId, state, from, size);
        return BookingMapper.toListBookingDto(bookings);
    }
}
