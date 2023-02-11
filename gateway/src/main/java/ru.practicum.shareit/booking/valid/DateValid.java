package ru.practicum.shareit.booking.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckDateValid.class)
public @interface DateValid {
    String message() default "Неверные данные";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
