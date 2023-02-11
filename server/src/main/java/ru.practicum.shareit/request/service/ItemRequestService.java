package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResult;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest itemRequest, Long userId);

    List<ItemRequestDtoResult> getAllUser(Long userId);

    List<ItemRequestDtoResult> getAll(Long userId, int from, int size);

    ItemRequestDtoResult getRequestId(Long userId, Long requestId);

}
