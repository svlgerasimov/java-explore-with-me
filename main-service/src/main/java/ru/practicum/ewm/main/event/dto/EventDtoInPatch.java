package ru.practicum.ewm.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
@NonFinal
@SuperBuilder
public class EventDtoInPatch {

    @Size(min = 20, max = 2000)
    String annotation;

    Long category;

    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    @Size(min = 3, max = 120)
    String title;
}
