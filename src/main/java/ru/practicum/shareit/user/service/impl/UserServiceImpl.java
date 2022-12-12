package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user, long userId) {
        User updateUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("пользователь не найден"));
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        return userRepository.save(updateUser);
    }

    @Override
    public User getById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("пользователь не найден"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(long userId) {
        getById(userId);
        userRepository.deleteById(userId);
    }
}
