package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.marker.Marker;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ConversionService conversionService;

    @PostMapping
    @Validated({Marker.Create.class})
    ItemDto add(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление вещи");
        Item newItem = ItemMapper.toItem(itemDto);
        Item item = itemService.add(newItem, userId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    @Validated({Marker.Update.class})
    ItemDto update(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-id") long userId,
                   @PathVariable long itemId) {
        log.info("Получен запрос от пользователя " + userId + " на редактирование вещи под идентификатором: " + itemId);
        Item newItem = ItemMapper.toItem(itemDto);
        Item item = itemService.update(newItem, userId, itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/{itemId}")
    ItemDto getItemById(@PathVariable long itemId) {
        log.info("Получен запрос от пользователя на просмотр вещи под идентификатором: " + itemId);
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя" + userId + " на просмотр всех своих вещей");
      List<Item> items = itemService.getAllItems(userId);
        return items.stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    List<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя" + userId + " на поиск вещи");
        List<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            items = itemService.search(text, userId);
        }
        return items.stream()
                .map(item -> conversionService.convert(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}
