package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoLastNext;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoLastNext lastBooking;
    private BookingDtoLastNext nextBooking;
    private List<CommentDto> comments;
}
