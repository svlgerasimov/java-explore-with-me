package ru.practicum.ewm.main.event.service;

import ru.practicum.ewm.main.event.dto.EventDtoIn;
import ru.practicum.ewm.main.event.dto.EventDtoInPatch;
import ru.practicum.ewm.main.event.dto.EventDtoOut;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;

import java.util.List;

public interface EventService {
    EventDtoOut add(Long userId, EventDtoIn eventDtoIn);

    List<EventDtoOutShort> findAllByInitiatorId(Long userId, Integer from, Integer size);

    EventDtoOut findByEventIdAndInitiatorId(Long eventId, Long userId);

    EventDtoOut patch(Long eventId, Long userId, EventDtoInPatch eventDtoInPatch);
}
