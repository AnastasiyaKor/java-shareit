package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResult;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private UserService userService;
    private Gson gson;
    private Item item;
    private User requestor;
    private User owner;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoRequest itemRequestDtoRequest;
    private ItemRequestDtoResult itemRequestDtoResult;
    private ItemItemRequestDto itemItemRequestDto;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
        owner = new User(1L, "Vova", "vova@mail.ru");
        requestor = new User(2L, "Nina", "nina@mail.ru");
        itemRequest = new ItemRequest(1L, "drill", requestor, LocalDateTime.now());
        item = new Item(1L, "drill", "drill pro", true, owner, itemRequest);
        itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor().getId(), itemRequest.getCreated());
        itemRequestDtoRequest = new ItemRequestDtoRequest(itemRequest.getId(), itemRequest.getDescription());
        itemItemRequestDto = new ItemItemRequestDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getRequestId().getId());
        itemRequestDtoResult = new ItemRequestDtoResult(1L, itemRequest.getDescription(), itemRequest.getCreated(),
                List.of(itemItemRequestDto));
    }

    @AfterEach
    void del() {
        owner = null;
        requestor = null;
        itemRequest = null;
        item = null;
        itemRequestDto = null;
        itemRequestDtoRequest = null;
        itemItemRequestDto = null;
        itemRequestDtoResult = null;
    }

    @Test
    void create() throws Exception {
        Mockito
                .when(userService.getById(requestor.getId()))
                .thenReturn(requestor);
        Mockito
                .when(itemRequestService.create(any(), any()))
                .thenReturn(itemRequest);
        mockMvc.perform(
                        post("/requests")
                                .content(gson.toJson(itemRequest))
                                .header("X-Sharer-User-Id", requestor.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getAllUser() throws Exception {
        Mockito
                .when(userService.getById(requestor.getId()))
                .thenReturn(requestor);
        Mockito
                .when(itemRequestService.getAllUser(any()))
                .thenReturn(List.of(itemRequestDtoResult));
        mockMvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", requestor.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        Mockito
                .when(userService.getById(requestor.getId()))
                .thenReturn(requestor);
        Mockito
                .when(itemRequestService.getAll(requestor.getId(), 0, 10))
                .thenReturn(List.of(itemRequestDtoResult));
        mockMvc.perform(
                        get("/requests/all?from=0&size=10")
                                .header("X-Sharer-User-Id", requestor.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getRequestId() throws Exception {
        Mockito
                .when(userService.getById(requestor.getId()))
                .thenReturn(requestor);
        Mockito
                .when(itemRequestService.getRequestId(requestor.getId(), itemRequest.getId()))
                .thenReturn(itemRequestDtoResult);
        mockMvc.perform(
                        get("/requests/{requestId}", itemRequest.getId())
                                .header("X-Sharer-User-Id", requestor.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoResult.getId()));
    }
}