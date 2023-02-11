package ru.practicum.shareit.booking.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                                @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос на добавление бронирования от пользователя" + userId);
        return bookingClient.createRequest(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatusRequest(@RequestHeader("X-Sharer-User-id") Long userId,
                                                      @RequestParam Boolean approved, @PathVariable Long bookingId) {
        log.info("Получен запрос на обновление статуса бронирования");
        return bookingClient.changeStatusRequest(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getId(@RequestHeader("X-Sharer-User-id") long userId,
                                        @PathVariable long bookingId) {
        log.info("Получен запрос на просмотр бронирования");
        return bookingClient.getId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingUser(@RequestHeader("X-Sharer-User-id") long userId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получен запрос на просмотр списка всех бронирований пользователя");

        return bookingClient.getAllBookingUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemsBookingUser(@RequestHeader("X-Sharer-User-id") long userId,
                                                         @RequestParam(defaultValue = "ALL") BookingState state,
                                                         @RequestParam(defaultValue = "0")
                                                         @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "10")
                                                         @Positive int size) {
        log.info("Получен запрос на просмотр списка бронирований для всех вещей пользователя");
        return bookingClient.getAllItemsBookingUser(userId, state, from, size);
    }
}
