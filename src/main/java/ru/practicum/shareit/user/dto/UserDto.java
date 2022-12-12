package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = {Marker.Create.class})
    private String name;
    @Email(groups = {Marker.Create.class, Marker.Update.class})
    @NotNull(groups = {Marker.Create.class}, message = "Почта не должна быть пустой")
    private String email;
}
