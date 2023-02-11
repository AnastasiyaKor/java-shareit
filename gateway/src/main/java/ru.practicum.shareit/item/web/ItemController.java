package ru.practicum.shareit.item.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Marker;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Validated({Marker.Create.class}) ItemDto itemDto,
                                      @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление вещи");
        return itemClient.add(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated @RequestBody CommentRequestDto commentRequestDto,
                                         @RequestHeader("X-Sharer-User-id") Long userId,
                                         @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление комментария");
        return itemClient.createComment(commentRequestDto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody @Validated({Marker.Update.class}) ItemDto itemDto,
                                  @RequestHeader("X-Sharer-User-id") Long userId,
                                  @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя " + userId + " на редактирование вещи под идентификатором: " + itemId);
        return itemClient.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByItemId(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос от пользователя " + userId + " на просмотр вещи под идентификатором: " + itemId);
        return itemClient.getByItemId(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-id") Long userId,
                                  @RequestParam(required = false, defaultValue = "0") int from,
                                  @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Получен запрос от пользователя " + userId + " на просмотр всех своих вещей");
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text, @RequestHeader("X-Sharer-User-id") long userId,
                                  @RequestParam(required = false, defaultValue = "0") int from,
                                  @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Получен запрос от пользователя " + userId + " на поиск вещи");
        return itemClient.search(userId, text, from, size);
    }
}
