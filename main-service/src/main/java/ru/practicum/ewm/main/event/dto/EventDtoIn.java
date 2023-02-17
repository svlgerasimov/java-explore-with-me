package ru.practicum.ewm.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.main.util.validation.After;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class EventDtoIn {

    @NotNull
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotNull
    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @After(hours = 2)
    LocalDateTime eventDate;

    @NotNull
    @Valid
    LocationDto location;

    @Builder.Default
    boolean paid = false;

    @Builder.Default
    @PositiveOrZero
    int participantLimit = 0;

    @Builder.Default
    boolean requestModeration = true;

    @NotNull
    @Size(min = 3, max = 120)
    String title;
}
