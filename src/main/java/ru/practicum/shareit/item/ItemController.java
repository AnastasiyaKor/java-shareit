package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    Item add(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя: " + userId + " на добавление вещи");
        Item newItem = ItemMapper.toItem(itemDto);
        return itemService.add(newItem, userId);
    }

    @PatchMapping("/{itemId}")
    Item update(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-id") long userId,
                @PathVariable long itemId) {
        log.info("Получен запрос от пользователя " + userId + " на редактирование вещи под идентификатором: " + itemId);
        Item newItem = ItemMapper.toItem(itemDto);
        return itemService.update(newItem, userId, itemId);
    }

    @GetMapping("/{itemId}")
    Item getItemById(@PathVariable long itemId) {
        log.info("Получен запрос от пользователя на просмотр вещи под идентификатором: " + itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    List<Item> getAllItems(@RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя" + userId + " на просмотр всех своих вещей");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    List<Item> search(@RequestParam String text, @RequestHeader("X-Sharer-User-id") long userId) {
        log.info("Получен запрос от пользователя" + userId + " на поиск вещи");
        return itemService.search(text, userId);
    }
}
