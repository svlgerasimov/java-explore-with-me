package ru.practicum.ewm.main.event.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.event.dto.*;

import java.util.List;

public interface EventService {
    EventDtoOut add(Long userId, EventDtoIn eventDtoIn);

    List<EventDtoOutShort> findAllByInitiatorId(Long userId, Integer from, Integer size);

    EventDtoOut findByEventIdAndInitiatorId(Long eventId, Long userId);

    EventDtoOut patchByInitiator(Long eventId, Long userId, EventDtoInInitiatorPatch eventDtoInInitiatorPatch);

    @Transactional
    EventDtoOut patchByAdmin(Long eventId, EventDtoInAdminPatch eventDtoInAdminPatch);
}
