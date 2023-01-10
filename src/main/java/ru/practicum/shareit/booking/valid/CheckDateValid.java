package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValid implements ConstraintValidator<DateValid, BookingRequestDto> {
    @Override
    public void initialize(DateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime end = bookingRequestDto.getEnd();
        LocalDateTime start = bookingRequestDto.getStart();
        if (end == null || start == null) {
            return false;
        }
        return (end.isAfter(start));
    }
}
