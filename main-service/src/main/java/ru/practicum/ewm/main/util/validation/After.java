package ru.practicum.ewm.main.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AfterValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface After {
    String message() default "Value must be fixed amount of time in future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int years() default 0;
    int months() default 0;
    int days() default 0;
    int hours() default 0;
    int minutes() default 0;
    int seconds() default 0;
}
