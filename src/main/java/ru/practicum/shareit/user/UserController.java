package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private  final  UserService userService;
    private final ConversionService conversionService;
    @PostMapping
    @Validated({Marker.Create.class})
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        User newUser = UserMapper.toUser(userDto);
        User user = userService.create(newUser);
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    @Validated({Marker.Update.class})
    public UserDto update(@PathVariable long userId, @RequestBody @Valid UserDto userDto) {
        log.info("Получен запрос на редактирование пользователя");
        User newUser = UserMapper.toUser(userDto);
        User user = userService.update(newUser, userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Получен запрос на просмотр пользователя");
        return UserMapper.toUserDto(userService.getUserById(userId));
    }

    @GetMapping
    public List<UserDto> findAllUser() {
        log.info("Получен запрос на просмотр всех пользователей");
        List<User> users = userService.findAllUser();
        return users.stream()
                .map(user -> conversionService.convert(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя");
        userService.delete(userId);
    }

}
