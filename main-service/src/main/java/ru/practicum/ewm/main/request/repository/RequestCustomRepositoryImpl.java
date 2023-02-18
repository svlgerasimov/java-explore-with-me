package ru.practicum.ewm.main.request.repository;

import org.springframework.context.annotation.Lazy;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.request.dto.RequestStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestCustomRepositoryImpl implements RequestCustomRepository {

    private final RequestRepository requestRepository;

    public RequestCustomRepositoryImpl(@Lazy RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Map<Long, Integer> findConfirmedRequestsCountsByEvent(List<EventEntity> eventEntities) {
        List<Long> eventIds = eventEntities.stream().map(EventEntity::getId).collect(Collectors.toList());
        return findConfirmedRequestsCountsByEventId(eventIds);
    }

    @Override
    public Map<Long, Integer> findConfirmedRequestsCountsByEventId(List<Long> eventIds) {
        List<RequestRepository.CountById> requestsCount =
                requestRepository.countByEventIdInAndStatus(eventIds, RequestStatus.CONFIRMED);
        return requestsCount.stream()
                .collect(Collectors.toMap(
                        RequestRepository.CountById::getId,
                        RequestRepository.CountById::getCount
                ));
    }
}
