package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    User update(User user, long userId);

    Optional<User> getUserById(long userId);

    List<User> findAllUser();

    void delete(long userId);
}
