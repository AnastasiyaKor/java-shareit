package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(User user, long userId);

    User getById(long userId);

    List<User> findAll();

    void delete(long userId);
}
