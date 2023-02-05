package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResult;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @PostMapping
    public ItemRequestDto create(@RequestBody @Validated ItemRequestDtoRequest itemRequestDtoRequest,
                                 @RequestHeader("X-Sharer-User-id") Long userId) {
        userService.getById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequestDtoRequest(itemRequestDtoRequest);
        ItemRequest itemRequestAdd = itemRequestService.create(itemRequest, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestAdd);
    }

    @GetMapping
    public List<ItemRequestDtoResult> getAllUser(@RequestHeader("X-Sharer-User-id") Long userId) {
        userService.getById(userId);

        return itemRequestService.getAllUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResult> getAll(@RequestHeader("X-Sharer-User-id") Long userId,
                                             @RequestParam(required = false, defaultValue = "0") int from,
                                             @RequestParam(required = false, defaultValue = "10") int size) {
        userService.getById(userId);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResult getRequestId(@RequestHeader("X-Sharer-User-id") Long userId,
                                             @PathVariable Long requestId) {
        userService.getById(userId);
        return itemRequestService.getRequestId(userId, requestId);
    }
}
