package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private final List<User> usersList = new ArrayList<>();
    private long createId = 0;

    @Override
    public User create(User user) {
        checkingMail(user);
        user.setId(++createId);
        usersList.add(user);
        return user;
    }

    @Override
    public User update(User user, long userId) {
        User newUser = new User();
        checkingMail(user);
        for (User u : usersList) {
            if (u.getId() == userId) {
                if (user.getName() != null && !user.getName().isBlank()) {
                    u.setName(user.getName());
                }
                if (user.getEmail() != null) {
                    u.setEmail(user.getEmail());
                }
                newUser = u;
            }
        }
        return newUser;
    }

    private void checkingMail(User user) {
        for (User uw : usersList) {
            if (user.getEmail() != null) {
                if (uw.getEmail().contains(user.getEmail())) {
                    throw new ConflictException("Почта уже существует");
                }
            }
        }
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(usersList.stream().filter(user -> user.getId() == userId)
                .findAny().orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    public List<User> findAll() {
        return usersList;
    }

    @Override
    public void delete(long userId) {
        usersList.removeIf(user -> user.getId() == userId);
    }
}
