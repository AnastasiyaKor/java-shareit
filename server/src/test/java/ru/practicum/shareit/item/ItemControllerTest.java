package ru.practicum.shareit.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.LocalDateTimeAdapter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoLastNext;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    private Gson gson;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private CommentRequestDto commentRequestDto;
    private ItemBookingDto itemBookingDto;
    private User owner;
    private Item item;
    private Booking booking;
    private User booker;
    private User bookerNext;
    private Comment comment;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
        owner = new User(1L, "Vova", "vova@mail.ru");
        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("drill pro")
                .available(true)
                .owner(owner)
                .build();
        booker = new User(3L, "Mark", "mark@mail.ru");
        bookerNext = new User(4L, "Gena", "gena@mail.ru");
        booking = new Booking(1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(5), item, booker, Status.APPROVED);
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(new UserDto(owner.getId(), owner.getName(), owner.getEmail()))
                .build();
        commentDto = new CommentDto(1L, "good", booker.getName(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        itemBookingDto = new ItemBookingDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                new BookingDtoLastNext(1L, User.builder().id(1L).build().getId()), new BookingDtoLastNext(2L,
                bookerNext.getId()), List.of(commentDto));
        comment = new Comment(1L, "good", item, booker, LocalDateTime.now());
        commentRequestDto = new CommentRequestDto(1L, "good");
    }

    @AfterEach
    void del() {
        owner = null;
        item = null;
        booker = null;
        bookerNext = null;
        booking = null;
        itemDto = null;
        commentDto = null;
        itemBookingDto = null;
        commentRequestDto = null;
    }


    @Test
    void add() throws Exception {
        Mockito
                .when(userService.getById(owner.getId()))
                .thenReturn(owner);
        Mockito
                .when(itemService.add(any(), any()))
                .thenReturn(item);
        mockMvc.perform(
                        post("/items")
                                .content(gson.toJson(item))
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void createComment() throws Exception {
        Mockito
                .when(itemService.createComment(commentRequestDto, owner.getId(), item.getId()))
                .thenReturn(comment);
        mockMvc.perform(
                        post("/items/{itemId}/comment", item.getId())
                                .content(gson.toJson(commentRequestDto))
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(commentRequestDto.getText())));
    }

    @Test
    void update() throws Exception {
        Mockito
                .when(userService.getById(owner.getId()))
                .thenReturn(owner);
        Mockito
                .when(itemService.update(any(), any(), any()))
                .thenReturn(item);
        item.setDescription("drill 200");
        mockMvc.perform(
                        patch("/items/{itemId}", item.getId())
                                .content(gson.toJson(item))
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void getByItemId() throws Exception {
        Mockito
                .when(itemService.getByItemId(item.getId(), owner.getId()))
                .thenReturn(itemBookingDto);
        mockMvc.perform(
                        get("/items/{itemId}", item.getId())
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemBookingDto.getId()));
    }

    @Test
    void getAll() throws Exception {
        Mockito
                .when(itemService.getAll(owner.getId(), 0, 10))
                .thenReturn(List.of(itemBookingDto));
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemBookingDto.getId()));
    }

    @Test
    void search() throws Exception {
        final String text = "drill";
        Mockito
                .when(itemService.getById(item.getId()))
                .thenReturn(item);
        Mockito
                .when(itemService.search(text, owner.getId(), 0, 10))
                .thenReturn(List.of(item));
        mockMvc.perform(
                        get("/items/search?text={text}", text)
                                .header("X-Sharer-User-Id", owner.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}