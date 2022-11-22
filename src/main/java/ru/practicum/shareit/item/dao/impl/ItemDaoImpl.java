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
    private final Map<Long, Item> newItem = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();

    @Override
    public Item add(Item item, long userId) {
        item.setId(++createId);
        final List<Item> items = userItemIndex.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        userItemIndex.put(userId, items);
        newItem.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        Item newItem1 = new Item();
        boolean check = false;
        for (Item i : newItem.values()) {
            if (i.getId() == itemId && i.getOwner().getId() == userId) {
                check = true;
                if (item.getName() != null && !item.getName().isBlank()) {
                    i.setName(item.getName());
                }
                if (item.getDescription() != null && !item.getDescription().isBlank()) {
                    i.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    i.setAvailable(item.getAvailable());
                }
                newItem1 = i;
            }
        }
        if (!check) {
            throw new NotFoundException("Вещь не найдена");
        }
        return newItem1;
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(newItem.values().stream().filter(item -> item.getId() == itemId)
                .findAny().orElseThrow(() -> new NotFoundException("Вещь не найдена")));
    }

    @Override
    public List<Item> getAllItems(long userId) {
        return newItem.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return newItem.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable().toString().contains("true"))
                .collect(Collectors.toList());
    }
}
