package ru.practicum.shareit.request.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated ItemRequestDtoRequest itemRequestDtoRequest,
                                         @RequestHeader("X-Sharer-User-id") Long userId) {
        return itemRequestClient.create(itemRequestDtoRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser(@RequestHeader("X-Sharer-User-id") Long userId) {
        return itemRequestClient.getAllUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-id") Long userId,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestId(@RequestHeader("X-Sharer-User-id") Long userId,
                                               @PathVariable Long requestId) {
        return itemRequestClient.getRequestId(userId, requestId);
    }
}
