package ru.practicum.shareit.user.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Marker;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated({Marker.Create.class}) UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                                         @RequestBody @Validated({Marker.Update.class}) UserDto userDto) {
        log.info("Получен запрос на редактирование пользователя");
        return userClient.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        log.info("Получен запрос на просмотр пользователя");
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Получен запрос на просмотр всех пользователей");
        return userClient.findAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя");
        userClient.delete(userId);
    }
}