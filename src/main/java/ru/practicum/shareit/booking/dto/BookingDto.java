package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
public class BookingDto {
    private final int id;
    private final LocalDate start;
    private final LocalDate end;
    private Item item;
    private User booker;
    private String status;
}
