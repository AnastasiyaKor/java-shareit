package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ConversionService conversionService;

    @PostMapping
    public ItemDto add(@RequestBody @Validated({Marker.Create.class}) ItemDto itemDto,
                @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление вещи");
        User user = userService.getById(userId);
        UserDto userDto = UserMapper.toUserDto(user);
        itemDto.setOwner(userDto);
        Item newItem = ItemMapper.toItem(itemDto);
        Item item = itemService.add(newItem, userId);
        return ItemMapper.toItemDto(item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Validated @RequestBody CommentRequestDto commentRequestDto,
                             @RequestHeader("X-Sharer-User-id") Long userId,
                             @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление комментария");
        itemService.getById(itemId);
        userService.getById(userId);
        Comment comment = itemService.createComment(commentRequestDto, userId, itemId);
        return CommentMapper.toCommentDto(comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody @Validated({Marker.Update.class}) ItemDto itemDto,
                   @RequestHeader("X-Sharer-User-id") Long userId,
                   @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя " + userId + " на редактирование вещи под идентификатором: " + itemId);
        User user = userService.getById(userId);
        UserDto userDto = UserMapper.toUserDto(user);
        itemDto.setOwner(userDto);
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.update(newItem, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getByItemId(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос от пользователя " + userId + " на просмотр вещи под идентификатором: " + itemId);
        return itemService.getByItemId(itemId, userId);
    }

    @GetMapping
    public List<ItemBookingDto> getAll(@RequestHeader("X-Sharer-User-id") Long userId,
                                @RequestParam(required = false,defaultValue = "0") int from,
                                @RequestParam(required = false,defaultValue = "10") int size) {
        log.info("Получен запрос от пользователя " + userId + " на просмотр всех своих вещей");
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-id") long userId,
                         @RequestParam(required = false,defaultValue = "0") int from,
                         @RequestParam(required = false,defaultValue = "10") int size) {
        log.info("Получен запрос от пользователя " + userId + " на поиск вещи");
        List<Item> items = Collections.emptyList();
        if (!text.isBlank()) {
            items = itemService.search(text, userId, from, size);
        }
        return items.stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}
