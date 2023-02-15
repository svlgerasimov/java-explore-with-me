package ru.practicum.ewm.main.compilations.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;

import java.util.List;

@Value
@Builder
@Jacksonized
public class CompilationDtoOut {

    List<EventDtoOutShort> events;
    Long id;
    Boolean pinned;
    String title;
}
