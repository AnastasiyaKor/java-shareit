package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemBookingDto;

import java.util.List;

public interface ItemService {
    Item add(Item item, Long userId);

    Item update(Item item, Long userId, Long itemId);

    Item getById(Long itemId);

    ItemBookingDto getByItemId(Long itemId, Long userId);

    List<ItemBookingDto> getAll(Long userId);

    List<Item> search(String text, long userId);

    Comment createComment(Comment comment/*, Long userId, Long itemId*/);

    List<Comment> getAllByItemId(Long itemId);

}
