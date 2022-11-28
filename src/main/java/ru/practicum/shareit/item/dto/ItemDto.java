package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class ItemDto {
    private long id;
    @NotBlank(groups = {Marker.Create.class})
    private String name;
    @NotBlank(groups = {Marker.Create.class})
    private String description;
    @NotNull(groups = {Marker.Create.class})
    private Boolean available;
    private Long owner;
    private Long request;

    public ItemDto() {
    }

    public ItemDto(long id, String name, String description, Boolean available, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
