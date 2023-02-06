package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStatusException;
import ru.practicum.shareit.exceptions.ValidatorException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.enumeration.BookingState.ALL;
import static ru.practicum.shareit.booking.enumeration.BookingState.UNSUPPORTED_STATUS;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    private Gson gson;
    private Booking booking;
    private Item item;
    private User booker;
    private User owner;
    private User user;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private ItemRequest itemRequest;
    private final ItemDtoLastNext itemDtoLastNext = new ItemDtoLastNext(1L, "drill");
    private final UserDtoRequest userDtoRequest = new UserDtoRequest(1L);

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .serializeNulls()
                .create();
        itemRequest = new ItemRequest(1L, "hammer", user, LocalDateTime.now());
        user = new User(3L, "Lev", "lev@mail.ru");
        booker = new User(1L, "Vova", "vova@mail.ru");
        owner = new User(2L, "Mark", "mark@mail.ru");
        item = new Item(1L, "drill", "drill pro", true, owner, itemRequest);
        booking = new Booking(1L, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(1),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(5), item, booker, Status.WAITING);
        bookingRequestDto = new BookingRequestDto(booking.getStart(), booking.getEnd(), item.getId());
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(5))
                .itemId(item.getId())
                .item(itemDtoLastNext)
                .booker(userDtoRequest)
                .status(Status.WAITING)
                .build();
    }

    @AfterEach
    void del() {
        itemRequest = null;
        user = null;
        booker = null;
        owner = null;
        item = null;
        booking = null;
        bookingRequestDto = null;
        bookingDto = null;
    }

    @Test
    void createRequest() throws Exception {
        Mockito
                .when(bookingService.createRequest(bookingRequestDto, booking.getId()))
                .thenReturn(booking);
        mockMvc.perform(
                        post("/bookings")
                                .content(gson.toJson(bookingRequestDto))
                                .header("X-Sharer-User-Id", booker.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookingRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingRequestDto.getEnd().toString())))
                .andExpect(jsonPath("$.itemId", is(1)));
    }

    @Test
    void createRequestUserNotFound() throws Exception {
        Mockito
                .when(bookingService.createRequest(any(), any()))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(
                        post("/bookings")
                                .content(gson.toJson(bookingRequestDto))
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void changeStatusRequest() throws Exception {
        booking.setStatus(Status.REJECTED);
        Mockito
                .when(bookingService.changeStatusRequest(booker.getId(), true, booking.getId()))
                .thenReturn(booking);
        mockMvc.perform(patch("/bookings/{id}?approved={approved}", booking.getId(), true)
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void changeStatusRequestValidStatus() throws Exception {
        booking.setStatus(Status.REJECTED);
        Mockito
                .when(bookingService.changeStatusRequest(booker.getId(), false, booking.getId()))
                .thenThrow(ValidatorException.class);
        mockMvc.perform(patch("/bookings/{id}?approved={approved}", booking.getId(), false)
                        .header("X-Sharer-User-Id", booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void getId() throws Exception {
        Mockito
                .when(bookingService.getId(booker.getId(), booking.getId()))
                .thenReturn(booking);
        mockMvc.perform(
                        get("/bookings/{id}", booking.getId())
                                .header("X-Sharer-User-Id", booker.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void getAllBookingUser() throws Exception {
        Mockito
                .when(bookingService.getAllBookingUser(booker.getId(), ALL, 0, 10))
                .thenReturn(List.of(booking));
        mockMvc.perform(
                        get("/bookings?state={state}&from={from}&size={size}", ALL, 0, 10)
                                .header("X-Sharer-User-Id", booker.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());
    }

    @Test
    void getAllBookingUserStatusIncorrect() throws Exception {
        Mockito
                .when(bookingService.getAllBookingUser(booker.getId(), UNSUPPORTED_STATUS, 0, 10))
                .thenThrow(UnknownStatusException.class);
        mockMvc.perform(
                        get("/bookings?state={state}&from={from}&size={size}",
                                UNSUPPORTED_STATUS, 0, 10)
                                .header("X-Sharer-User-Id", booker.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemsBookingUser() throws Exception {
        Mockito
                .when(bookingService.getAllItemsBookingUser(owner.getId(), ALL, 0, 10))
                .thenReturn(List.of(booking));
        mockMvc.perform(
                        get("/bookings/owner?state={state}&from={from}&size={size}", ALL, 0, 10)
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());
    }

    @Test
    void getAllItemsBookingUserStatusIncorrect() throws Exception {
        Mockito
                .when(bookingService.getAllItemsBookingUser(owner.getId(), UNSUPPORTED_STATUS, 0, 10))
                .thenThrow(UnknownStatusException.class);
        mockMvc.perform(
                        get("/bookings/owner?state={state}&from={from}&size={size}",
                                UNSUPPORTED_STATUS, 0, 10)
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemsBookingUserIncorrect() throws Exception {
        Mockito
                .when(bookingService.getAllItemsBookingUser(owner.getId(), ALL, 0, 0))
                .thenThrow(ConstraintViolationException.class);
        mockMvc.perform(
                        get("/bookings/owner?state={state}&from={from}&size={size}",
                                ALL, 0, 0)
                                .header("X-Sharer-User-Id", owner.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}