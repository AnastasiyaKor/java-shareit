package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")

public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        User user = UserMapper.toUser(userDto);
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос на редактирование пользователя");
        User newUser = UserMapper.toUser(userDto);
        return userService.update(newUser, userId);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        log.info("Получен запрос на просмотр пользователя");
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<User> findAllUser() {

        log.info("Получен запрос на просмотр всех пользователей");
        return userService.findAllUser();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя");
        userService.delete(userId);
    }

}
