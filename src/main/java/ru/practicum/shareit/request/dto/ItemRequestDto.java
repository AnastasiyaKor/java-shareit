package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private final int id;
    private final String description;
    private User requestor;
    private final LocalDateTime created;
}
