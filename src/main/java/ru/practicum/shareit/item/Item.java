package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long request;

    public Item(long id, String name, String description, Boolean available, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
