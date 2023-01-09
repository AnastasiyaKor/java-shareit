package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoLastNext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;
import ru.practicum.shareit.user.dto.UserDtoRequest;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                new ItemDtoLastNext(booking.getItem().getId(), booking.getItem().getName()),
                new UserDtoRequest(booking.getBooker().getId()),
                booking.getStatus()
        );
    }

    public static Booking fromBookingRequestDto(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .id(bookingRequestDto.getId())
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }

    public static BookingDtoLastNext toBookingDtoLastNext(Booking booking) {
        return new BookingDtoLastNext(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

}
