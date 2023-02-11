package ru.practicum.shareit.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.LocalDateTimeAdapter;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private Gson gson;
    private User user;
    private User user1;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
        user = User.builder()
                .id(1L)
                .name("Vova")
                .email("vova@mail.ru")
                .build();
        user1 = User.builder()
                .id(2L)
                .name("Mark")
                .email("mark@mail.ru")
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Test
    void create() throws Exception {
        Mockito
                .when(userService.create(any()))
                .thenReturn(user);
        mockMvc.perform(
                        post("/users")
                                .content(gson.toJson(user))
                                .header("X-Sharer-User-Id", user.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void createValidatorException() throws Exception {
        Mockito.when(userService.create(any()))
                .thenThrow(ValidatorException.class);
        User notEmail = User.builder()
                .name("Lev")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(gson.toJson(notEmail))
                                .header("X-Sharer-User-Id", user.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createIncorrectEmail() throws Exception {
        Mockito.when(userService.create(any()))
                .thenThrow(ValidatorException.class);
        User incorrectEmail = User.builder()
                .name("Lev")
                .email("Levmail.ru")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(gson.toJson(incorrectEmail))
                                .header("X-Sharer-User-Id", user.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        Mockito
                .when(userService.update(any(), anyLong()))
                .thenReturn(user);
        user.setName("Vladimir");
        mockMvc.perform(
                        patch("/users/{userId}", user.getId())
                                .content(gson.toJson(user))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())));
    }

    @Test
    void getById() throws Exception {
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(user);
        mockMvc.perform(
                        get("/users/{userId}", user.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void findAll() throws Exception {
        Mockito
                .when(userService.findAll())
                .thenReturn(List.of(user));
        mockMvc.perform(
                        get("/users")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
        Mockito
                .when(userService.getById(anyLong()))
                .thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}