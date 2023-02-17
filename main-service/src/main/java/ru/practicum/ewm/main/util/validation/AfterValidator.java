package ru.practicum.ewm.main.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AfterValidator implements ConstraintValidator<After, LocalDateTime> {

    private int years;
    private int months;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;

    @Override
    public void initialize(After constraintAnnotation) {
        years = constraintAnnotation.years();
        months = constraintAnnotation.months();
        days = constraintAnnotation.days();
        hours = constraintAnnotation.hours();
        minutes = constraintAnnotation.minutes();
        seconds = constraintAnnotation.seconds();
    }

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime limit = LocalDateTime.now()
                .plusYears(years)
                .plusMonths(months)
                .plusDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);

        return localDateTime.isAfter(limit);
    }
}
