package ru.practicum.ewm.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.main.util.validation.NullableNotBlank;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class EventDtoInPatch {

    @NullableNotBlank
    String annotation;

    Long category;

    @NullableNotBlank
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    EventStateAction stateAction;

    @NullableNotBlank
    String title;
}
