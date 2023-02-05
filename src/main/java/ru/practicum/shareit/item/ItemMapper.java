package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoLastNext;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
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
                item.getRequestId() != null ? item.getRequestId().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(User.builder().id(itemDto.getOwner().getId())
                        .name(itemDto.getOwner().getName())
                        .email(itemDto.getOwner().getEmail())
                        .build())
                .requestId(itemDto.getRequestId() != null ?
                        ItemRequest.builder()
                                .id(itemDto.getRequestId())
                                .build() : null)
                .build();
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

    public static ItemItemRequestDto toItemItemRequestDto(Item item) {
        return new ItemItemRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId().getId()
        );
    }
}
