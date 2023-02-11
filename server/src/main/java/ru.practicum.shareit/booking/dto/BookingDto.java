package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private ItemDtoLastNext item;
    private UserDtoRequest booker;
    private Status status;
}
