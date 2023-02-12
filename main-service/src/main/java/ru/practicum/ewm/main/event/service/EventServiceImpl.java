package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.event.dto.EventDtoIn;
import ru.practicum.ewm.main.event.dto.EventDtoInPatch;
import ru.practicum.ewm.main.event.dto.EventDtoOut;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    @Override
    public EventDtoOut add(Long userId, EventDtoIn eventDtoIn) {
        return null;
    }

    @Override
    public List<EventDtoOutShort> findAllByInitiatorId(Long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventDtoOut findByEventIdAndInitiatorId(Long eventId, Long userId) {
        return null;
    }

    @Override
    public EventDtoOut patch(Long eventId, Long userId, EventDtoInPatch eventDtoInPatch) {
        return null;
    }
}
