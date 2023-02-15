package ru.practicum.ewm.main.request.repository;

import ru.practicum.ewm.main.event.model.EventEntity;

import java.util.List;
import java.util.Map;

public interface RequestCustomRepository {
    Map<Long, Integer> findConfirmedRequestsCountsByEvent(List<EventEntity> eventEntities);

    Map<Long, Integer> findConfirmedRequestsCountsByEventId(List<Long> eventIds);
}
