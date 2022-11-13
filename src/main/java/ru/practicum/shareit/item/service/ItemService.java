package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item, long userId);

    Item update(Item item, long userId, long itemId);

    Item getItemById(long itemId);

    List<Item> getAllItems(long userId);

    List<Item> search(String text, long userId);
}
