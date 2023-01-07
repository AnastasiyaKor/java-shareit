package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.booking.valid.DateValid;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DateValid
public class BookingDto {
    private Long id;
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;
    private Long itemId;
    private ItemDtoLastNext item;
    private UserDtoRequest booker;
    private Status status;
}
