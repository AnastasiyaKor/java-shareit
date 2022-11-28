package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.marker.Marker;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ConversionService conversionService;

    @PostMapping
    ItemDto add(@RequestBody @Validated({Marker.Create.class}) ItemDto itemDto,
                @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление вещи");
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.add(newItem, userId));
    }

    @PatchMapping("/{itemId}")
    ItemDto update(@RequestBody @Validated({Marker.Update.class}) ItemDto itemDto,
                   @RequestHeader("X-Sharer-User-id") long userId,
                   @PathVariable long itemId) {
        log.info("Получен запрос от пользователя " + userId +
                " на редактирование вещи под идентификатором: " + itemId);
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.update(newItem, userId, itemId));
    }

    @GetMapping("/{itemId}")
    ItemDto getById(@PathVariable long itemId) {
        log.info("Получен запрос от пользователя на просмотр вещи под идентификатором: " + itemId);
        return ItemMapper.toItemDto(itemService.getById(itemId));
    }

    @GetMapping
    List<ItemDto> getAll(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя" + userId + " на просмотр всех своих вещей");
        return itemService.getAll(userId).stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
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
