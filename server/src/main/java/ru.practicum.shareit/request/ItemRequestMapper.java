package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResult;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequestDtoRequest(ItemRequestDtoRequest itemRequestDtoRequest) {
        return ItemRequest.builder()
                .id(itemRequestDtoRequest.getId())
                .description(itemRequestDtoRequest.getDescription())
                .build();
    }

    public static ItemRequestDtoResult toItemRequestItemDto(
            ItemRequest itemRequest, List<ItemItemRequestDto> itemItemRequestDto) {
        return new ItemRequestDtoResult(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemItemRequestDto
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                User.builder()
                        .id(itemRequest.getRequestor().getId())
                        .name(itemRequest.getRequestor().getName())
                        .email(itemRequest.getRequestor().getEmail())
                        .build().getId(),
                itemRequest.getCreated()
        );
    }
}
