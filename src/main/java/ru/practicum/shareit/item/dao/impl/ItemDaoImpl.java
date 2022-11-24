package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemDaoImpl implements ItemDao {
    private long createId = 0;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    @Override
    public Item add(Item item, long userId) {
        item.setId(++createId);
        userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        if (items.containsValue(items.get(itemId)) && items.get(itemId).getOwner().getId() == userId) {
            if (item.getName() != null && !item.getName().isBlank()) {
                items.get(itemId).setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                items.get(itemId).setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                items.get(itemId).setAvailable(item.getAvailable());
            }
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
        return items.get(itemId);
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable())
                .collect(Collectors.toList());
    }
}
