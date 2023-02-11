package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoLastNextTest {
    @Autowired
    private JacksonTester<BookingDtoLastNext> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new BookingDtoLastNext(1L, 2L);
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(dto.getBookerId().intValue());
    }
}