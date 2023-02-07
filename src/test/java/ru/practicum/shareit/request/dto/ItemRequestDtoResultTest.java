package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoResultTest {
    @Autowired
    JacksonTester<ItemRequestDtoResult> json;

    @Test
    void testSerialize() throws Exception {
        User owner = new User(1L, "Lev", "lev@mail.ru");
        User requestor = new User(2L, "Vova", "vova@mail.ru");
        ItemRequest request = new ItemRequest(1L, "drill", requestor,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Item item = Item.builder()
                .id(1L)
                .name("drill")
                .description("drellPro")
                .owner(owner)
                .available(true)
                .requestId(request)
                .build();
        ItemItemRequestDto itemItemRequestDto = new ItemItemRequestDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequestId().getId());
        var dto = new ItemRequestDtoResult(1L, "drillPro",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), List.of(itemItemRequestDto));
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(dto.getCreated().toString());
        assertThat(result).extractingJsonPathValue("$.items").isNotNull();
    }
}