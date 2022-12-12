package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.marker.Marker;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Marker.Create.class})
    private String name;
    @NotBlank(groups = {Marker.Create.class})
    private String description;
    @NotNull(groups = {Marker.Create.class})
    private Boolean available;
    private UserDto owner;
    private Long request;
}
