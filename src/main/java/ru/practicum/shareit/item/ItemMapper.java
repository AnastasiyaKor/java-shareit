package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoLastNext;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new UserDto(item.getOwner().getId(), item.getOwner().getName(), item.getOwner().getEmail()),
                item.getRequest()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                User.builder().id(itemDto.getOwner().getId())
                        .name(itemDto.getOwner().getName())
                        .email(itemDto.getOwner().getEmail())
                        .build(),
                itemDto.getRequest());
    }

    public static ItemBookingDto toItemBookingDto(Item item, BookingDtoLastNext lastBooking,
                                                  BookingDtoLastNext nextBooking, List<CommentDto> commentDto) {
        return new ItemBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                commentDto
        );
    }
}
