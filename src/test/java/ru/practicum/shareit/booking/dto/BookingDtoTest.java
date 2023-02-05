package ru.practicum.shareit.booking.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.LocalDateTimeAdapter;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enumeration.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> testerDto;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .create();
    private Item item;
    private Booking booking;
    private User owner;
    private User booker;
    BookingDto bookingDto;
    String content;
    BookingDto result;

    @BeforeEach
    void setUp() throws IOException {
        owner = new User(1L, "Mark", "Mark@mail.ru");
        booker = new User(2l, "Vova", "vova@mail.ru");
        item = Item.builder()
                .id(1L)
                .name("drill")
                .description("drill pro")
                .available(true)
                .owner(owner)
                .build();
        booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5), item, booker, Status.APPROVED);
        bookingDto = BookingMapper.toBookingDto(booking);
        content = gson.toJson(bookingDto);
        result = testerDto.parse(content).getObject();
    }

    @Test
    void getId() {
        assertThat(result.getId()).isEqualTo(bookingDto.getId());
    }

    @Test
    void getStart() {
        assertThat(result.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(result.getStart()).isBefore(bookingDto.getEnd());

    }

    @Test
    void getEnd() {
        assertThat(result.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(result.getEnd()).isAfter(bookingDto.getStart());
    }

    @Test
    void getItemId() {
        assertThat(result.getItemId()).isEqualTo(bookingDto.getItemId());
    }

    @Test
    void getItem() {
        assertThat(result.getItem()).isEqualTo(bookingDto.getItem());

    }

    @Test
    void getBooker() {
        assertThat(result.getBooker().getId()).isEqualTo(bookingDto.getBooker().getId());
    }

    @Test
    void getStatus() {
        assertThat(result.getStatus()).isEqualTo(bookingDto.getStatus());
    }

    @Test
    void setId() {
        booking.setId(2L);
        assertThat(result.getId()).isEqualTo(bookingDto.getId());
    }

    @Test
    void setStart() {
        booking.setStart(LocalDateTime.now().minusDays(15));
        assertThat(result.getStart()).isEqualTo(bookingDto.getStart());
    }

    @Test
    void setEnd() {
        booking.setEnd(LocalDateTime.now().plusDays(20));
        assertThat(result.getEnd()).isEqualTo(bookingDto.getEnd());
    }
}