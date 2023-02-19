package ru.practicum.ewm.main.event.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDtoOutPrivate add(Long userId, EventDtoIn eventDtoIn);

    List<EventDtoOutShort> findAllByInitiatorId(Long userId, Integer from, Integer size);

    EventDtoOutPrivate findByEventIdAndInitiatorId(Long eventId, Long userId);

    EventDtoOutPrivate patchByInitiator(Long eventId, Long userId, EventDtoInInitiatorPatch eventDtoInInitiatorPatch);

    @Transactional
    EventDtoOutPrivate patchByAdmin(Long eventId, EventDtoInAdminPatch eventDtoInAdminPatch);

    List<EventDtoOutPrivate> findByFiltersAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size);

    List<EventDtoOutPrivate> findAllPending(Integer from, Integer size);

    EventDtoOutPublic findPublishedEventById(Long eventId, HttpServletRequest httpServletRequest);

    List<EventDtoOutShort> findPublishedEventsByFilters(String text, List<Long> categories, Boolean paid,
                                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                        Boolean onlyAvailable, EventSortType sortType,
                                                        Integer from, Integer size,
                                                        HttpServletRequest httpServletRequest);
}
