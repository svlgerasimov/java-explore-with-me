package ru.practicum.ewm.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;
import ru.practicum.ewm.main.user.dto.UserDtoOutShort;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class EventDtoOutShort {

    String annotation;

    CategoryDtoOut category;

    Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Long id;

    UserDtoOutShort initiator;

    Boolean paid;

    String title;

    Long views;
}
