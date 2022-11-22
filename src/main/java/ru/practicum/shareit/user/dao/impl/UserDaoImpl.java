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
    private final List<User> userList = new ArrayList<>();
    private long createId = 0;

    @Override
    public User create(User user) {
        checkingMail(user);
        user.setId(++createId);
        userList.add(user);
        return user;
    }


    @Override
    public User update(User user, long userId) {
        User newUser = new User();
        checkingMail(user);
        for (User u : userList) {
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
        for (User uw : userList) {
            if (user.getEmail() != null) {
                if (uw.getEmail().contains(user.getEmail())) {
                    throw new ConflictException("Почта уже существует");
                }
            }
        }
    }

    @Override
    public Optional<User> getUserById(long userId) {

        return Optional.ofNullable(userList.stream().filter(user -> user.getId() == userId)
                .findAny().orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    public List<User> findAllUser() {
        return userList;
    }

    @Override
    public void delete(long userId) {

        userList.removeIf(user -> user.getId() == userId);
    }
}
