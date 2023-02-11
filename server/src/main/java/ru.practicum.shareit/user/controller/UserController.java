package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ConversionService conversionService;

    @PostMapping
    public UserDto create(@RequestBody @Validated({Marker.Create.class}) UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        User newUser = UserMapper.toUser(userDto);
        User user = userService.create(newUser);
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody @Validated({Marker.Update.class}) UserDto userDto) {
        log.info("Получен запрос на редактирование пользователя");
        User newUser = UserMapper.toUser(userDto);
        User user = userService.update(newUser, userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.info("Получен запрос на просмотр пользователя");
        return UserMapper.toUserDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен запрос на просмотр всех пользователей");
        List<User> users = userService.findAll();
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
