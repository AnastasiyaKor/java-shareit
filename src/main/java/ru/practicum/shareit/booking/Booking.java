package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    private final long id;
    private final LocalDate start;
    private final LocalDate end;
    private Item item;
    private User booker;
    private Status status;
}
