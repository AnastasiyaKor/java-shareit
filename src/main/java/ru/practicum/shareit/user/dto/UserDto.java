package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {Marker.Create.class})
    private String name;
    @NotBlank(groups = {Marker.Create.class}, message = "Почта не должна быть пустой")
    @Email(groups = {Marker.Create.class, Marker.Update.class})
    private String email;
}
