package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ItemRequest {
    private final long id;
    private final String description;
    private User requestor;
    private final LocalDateTime created;

}