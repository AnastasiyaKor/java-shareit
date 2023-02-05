package ru.practicum.shareit.convertor;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemToItemDto implements Converter<Item, ItemDto> {

    @Override
    public ItemDto convert(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequestId() != null ? item.getRequestId().getId() : null);
        return itemDto;
    }
}
