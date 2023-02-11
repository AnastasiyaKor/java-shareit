package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRequestDto that = (BookingRequestDto) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, itemId);
    }
}
