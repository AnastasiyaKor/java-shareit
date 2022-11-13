package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(User user, long userId);

    User getUserById(long userId);

    List<User> findAllUser();

    void delete(long userId);
}
