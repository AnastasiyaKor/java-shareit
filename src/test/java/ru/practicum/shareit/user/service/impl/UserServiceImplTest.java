package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void init() {
        user = new User(1L, "Masha", "Masha@mail.ru");
    }

    @AfterEach
    void del() {
        user = null;
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);
        User userSave = userService.create(user);
        assertEquals(userSave, user);
    }

    @Test
    void update() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        User userUpdate = userService.update(user, user.getId());
        if (user.getName() != null && !user.getName().isBlank()) {
            assertEquals(userUpdate.getName(), user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            assertEquals(userUpdate.getEmail(), user.getEmail());
        }
    }

    @Test
    void updateUserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.update(user, user.getId());
        });
        String message = exception.getMessage();
        String actualMessage = "пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @Test
    void getById() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        User userGetId = userService.getById(user.getId());
        assertEquals(userGetId, user);
    }

    @Test
    void getByIdNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getById(user.getId());
        });
        String message = exception.getMessage();
        String actualMessage = "пользователь не найден";
        assertEquals(message, actualMessage);
    }

    @Test
    void findAll() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<User> users = userService.findAll();
        assertEquals(users, List.of(user));
    }

    @Test
    void delete() {
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        userService.delete(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}