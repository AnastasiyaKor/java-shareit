package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidatorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemDaoImpl implements ItemDao {
    private long createId = 0;
    private final List<Item> itemsList = new ArrayList<>();

    @Override
    public Item add(Item item, long userId) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getDescription() == null) {
            throw new ValidatorException("Не указан статус");
        }
        item.setId(++createId);
        item.setOwner(userId);
        itemsList.add(item);
        return item;
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        Item newItem = new Item();
        boolean check = false;
        for (Item i : itemsList) {
            if (i.getId() == itemId && i.getOwner() == userId) {
                check = true;
                if (item.getName() != null) {
                    if (!item.getName().isBlank()) {
                        i.setName(item.getName());
                    }
                }
                if (item.getDescription() != null) {
                    i.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    i.setAvailable(item.getAvailable());
                }
                newItem = i;
            }
        }
        if (!check) {
            throw new NotFoundException("Вещь не найдена");
        }
        return newItem;
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(itemsList.stream().filter(item -> item.getId() == itemId)
                .findAny().orElseThrow(() -> new NotFoundException("Вещь не найдена")));
    }

    @Override
    public List<Item> getAllItems(long userId) {
        return itemsList.stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        List<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : itemsList) {
                if (((item.getName().toLowerCase()).contains(text.toLowerCase()) ||
                        (item.getDescription().toLowerCase()).contains(text.toLowerCase()))
                        && item.getAvailable().toString().contains("true")) {
                    items.add(item);
                }
            }
        }
        return items;
    }
}
