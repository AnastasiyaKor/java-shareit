package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enumeration.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;

    @PostMapping
    public BookingDto createRequest(@RequestBody BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос на добавление бронирования");
        UserDtoRequest userDtoRequest = new UserDtoRequest();
        userDtoRequest.setId(userId);
        bookingDto.setBooker(userDtoRequest);

        ItemDtoLastNext itemDtoRequest = new ItemDtoLastNext();
        itemDtoRequest.setId(bookingDto.getItemId());
        Item item = itemService.getById(bookingDto.getItemId());
        itemDtoRequest.setName(item.getName());
        bookingDto.setItem(itemDtoRequest);
        Booking newBooking = BookingMapper.toBooking(bookingDto);
        Booking booking = bookingService.createRequest(newBooking, userId);
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
                                              @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("Получен запрос на просмотр списка всех бронирований пользователя");
        List<Booking> bookings = bookingService.getAllBookingUser(userId, state);
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsBookingUser(@RequestHeader("X-Sharer-User-id") long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        log.info("Получен запрос на просмотр списка бронирований для всех вещей пользователя");
        List<Booking> bookings = bookingService.getAllItemsBookingUser(userId, state);
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
