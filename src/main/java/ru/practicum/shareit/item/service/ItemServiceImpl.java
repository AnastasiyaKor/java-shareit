package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public Item add(Item item, long userId) {
        userDao.getById(userId);
        item.setOwner(userDao.getById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден")));
        return itemDao.add(item, userId);
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        userDao.getById(userId);
        itemDao.getById(itemId);
        item.setOwner(userDao.getById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден")));
        return itemDao.update(item, userId, itemId);
    }

    @Override
    public Item getById(long itemId) {
        return itemDao.getById(itemId).orElseThrow(() ->
                new NotFoundException("вещь не найдена"));
    }

    @Override
    public List<Item> getAll(long userId) {
        return itemDao.getAll(userId);
    }

    @Override
    public List<Item> search(String text, long userId) {
        userDao.getById(userId);
        return itemDao.search(text);
    }
}
