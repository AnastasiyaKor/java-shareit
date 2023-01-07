package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
//@AllArgsConstructor
@Transactional(readOnly = true)//добавлен
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional//добавлен
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional//добавлен
    public User update(User user, long userId) {
        User updateUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("пользователь не найден"));
        //добавлен
        if (user.getName() != null && !user.getName().isBlank()) {
            updateUser.setName(user.getName());
        }
        //добавлен
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updateUser.setEmail(user.getEmail());
        }
        return updateUser;//userRepository.save(updateUser);
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
    @Transactional//добавлен
    public void delete(long userId) {
        getById(userId);
        userRepository.deleteById(userId);
    }
}
