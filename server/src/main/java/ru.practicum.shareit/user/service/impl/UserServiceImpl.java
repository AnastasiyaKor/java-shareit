package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user, long userId) {
        User updateUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("пользователь не найден"));
        if (user.getName() != null && !user.getName().isBlank()) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updateUser.setEmail(user.getEmail());
        }
        return updateUser;
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
    @Transactional
    public void delete(long userId) {
        getById(userId);
        userRepository.deleteById(userId);
    }
}
