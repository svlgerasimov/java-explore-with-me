package ru.practicum.ewm.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class EventDtoIn {

    @NotBlank
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    LocalDateTime eventDate;

    @NotNull
    @Valid
    LocationDto location;

    @Builder.Default
    Boolean paid = false;

    @Builder.Default
    Integer participantLimit = 0;

    @Builder.Default
    Boolean requestModeration = true;

    @NotBlank
    String title;
}
