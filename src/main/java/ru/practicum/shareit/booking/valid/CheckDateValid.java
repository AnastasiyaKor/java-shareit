package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValid implements ConstraintValidator<DateValid, BookingDto> {
    @Override
    public void initialize(DateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        final LocalDateTime date = LocalDateTime.now();
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime start = bookingDto.getStart();
        return (end.isAfter(date) && end.isAfter(start) && start.isAfter(date));
    }
}
