package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item add(Item item, long userId);

    Item update(Item item, long userId, long itemId);

    Optional<Item> getItemById(long itemId);

    List<Item> getAllItems(long userId);

    List<Item> search(String text);
}
