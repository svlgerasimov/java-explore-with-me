package ru.practicum.ewm.main.event.repository;

import ru.practicum.ewm.main.event.dto.EventState;
import ru.practicum.ewm.main.event.model.EventEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCustomRepository {
    List<EventEntity> findByFiltersAdmin(List<Long> users,
                                         List<EventState> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size);

    List<EventEntity> findPublishedEventsByFiltersOrderByDate(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   Integer from, Integer size);

    List<Long> findIdsOfPublishedEventsByFilters(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable);
}
