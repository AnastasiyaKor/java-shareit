package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private final Long id;
    @NotNull
    private final String description;
    private final Long requestorId;
    @FutureOrPresent
    private final LocalDateTime created;

}
