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
        userDao.getUserById(userId);
        item.setOwner(userDao.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден")));
        return itemDao.add(item, userId);
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        userDao.getUserById(userId);
        itemDao.getItemById(itemId);
        item.setOwner(userDao.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден")));
        return itemDao.update(item, userId, itemId);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemDao.getItemById(itemId).orElseThrow(() ->
                new NotFoundException("вещь не найдена"));
    }

    @Override
    public List<Item> getAllItems(long userId) {
        return itemDao.getAllItems(userId);
    }

    @Override
    public List<Item> search(String text, long userId) {
        userDao.getUserById(userId);
        return itemDao.search(text);
    }
}
