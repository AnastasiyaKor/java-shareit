package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public User create(User user) {
        return userDao.create(user);
    }

    @Override
    public User update(User user, long userId) {
        getUserById(userId);
        return userDao.update(user, userId);
    }

    @Override
    public User getUserById(long userId) {
        return userDao.getUserById(userId).orElseThrow(() ->
                new NotFoundException("пользователь не найден"));
    }

    @Override
    public List<User> findAllUser() {
        return userDao.findAllUser();
    }

    @Override
    public void delete(long id) {
        getUserById(id);
        userDao.delete(id);
    }
}
