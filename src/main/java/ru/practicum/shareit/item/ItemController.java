package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.marker.Marker;
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
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ConversionService conversionService;

    @PostMapping
    ItemDto add(@RequestBody @Validated({Marker.Create.class}) ItemDto itemDto,
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
    CommentDto createComment(@Validated @RequestBody CommentDto commentDto,
                             @RequestHeader("X-Sharer-User-id") Long userId,
                             @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление комментария");
        Item item = itemService.getById(itemId);
        User user = userService.getById(userId);
        Comment comment = itemService.createComment(CommentMapper.toComment(commentDto, item, user));
        return CommentMapper.toCommentDto(comment);
    }

    @PatchMapping("/{itemId}")
    ItemDto update(@RequestBody @Validated({Marker.Update.class}) ItemDto itemDto,
                   @RequestHeader("X-Sharer-User-id") Long userId,
                   @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя " + userId +
                " на редактирование вещи под идентификатором: " + itemId);
        User user = userService.getById(userId);
        UserDto userDto = UserMapper.toUserDto(user);
        itemDto.setOwner(userDto);
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.update(newItem, userId, itemId));
    }

    @GetMapping("/{itemId}")
    ItemBookingDto getByItemId(@RequestHeader("X-Sharer-User-id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя на просмотр вещи под идентификатором: " + itemId);
        return itemService.getByItemId(itemId, userId);
    }

    @GetMapping
    List<ItemBookingDto> getAll(@RequestHeader("X-Sharer-User-id") Long userId) {
        log.info("Получен запрос от пользователя" + userId + " на просмотр всех своих вещей");
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    List<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя" + userId + " на поиск вещи");
        List<Item> items = Collections.emptyList();
        if (!text.isBlank()) {
            items = itemService.search(text, userId);
        }
        return items.stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}
