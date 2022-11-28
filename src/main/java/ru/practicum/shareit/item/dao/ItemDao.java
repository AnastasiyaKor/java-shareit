package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Item add(Item item, long userId);

    Item update(Item item, long userId, long itemId);

    Optional<Item> getById(long itemId);

    List<Item> getAll(long userId);

    List<Item> search(String text);
}
