package ru.practicum.ewm.main.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientException;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.EventDtoMapper;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.model.QEventEntity;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.NotImplementedException;
import ru.practicum.ewm.main.request.dto.RequestStatus;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.user.model.UserEntity;
import ru.practicum.ewm.main.user.repository.UserRepository;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventDtoMapper eventDtoMapper;
    private final StatClient statClient;

    private static final LocalDateTime MIN_DATE =
            LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime MAX_DATE =
            LocalDateTime.of(9999, 12, 31, 23, 59);
    private static boolean UNIQUE_VIEWS = false;

    @Override
    @Transactional
    public EventDtoOut add(Long userId, EventDtoIn eventDtoIn) {
        LocalDateTime eventDate = eventDtoIn.getEventDate();
        LocalDateTime now = LocalDateTime.now();
        checkEventDate(eventDate, now);
        UserEntity initiator = findUserEntity(userId);
        CategoryEntity category = findCategoryEntity(eventDtoIn.getCategory());
        EventEntity eventEntity = eventDtoMapper.createFromDto(eventDtoIn, initiator, category, now);
        eventEntity = eventRepository.save(eventEntity);

        EventDtoOut eventDtoOut = eventDtoMapper.toDtoFull(eventEntity, null, null);
        log.debug("Add event {}", eventDtoOut);
        return eventDtoOut;
    }

    @Override
    public List<EventDtoOutShort> findAllByInitiatorId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventEntity> eventEntities = eventRepository.findAllByInitiatorId(userId, pageable);
        List<Long> eventIds = eventEntities.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());

        Map<Long, Integer> requestCountsByEventId = findConfirmedRequestsCountsByEventId(eventEntities);
        Map<Long, Long> viewCountsByEventId = getViewsCountByEventIdFromStatistics(eventIds, UNIQUE_VIEWS);

        log.debug(eventEntities.toString());
        return eventDtoMapper.toDtoShort(eventEntities, requestCountsByEventId, viewCountsByEventId);
    }

    @Override
    public EventDtoOut findByEventIdAndInitiatorId(Long eventId, Long userId) {
        EventEntity eventEntity = findEventEntityByIdAndInitiatorId(eventId, userId);
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = getViewsCountFromStatistics(eventId, UNIQUE_VIEWS);
        return eventDtoMapper.toDtoFull(eventEntity, confirmedRequests, views);
    }

    @Override
    @Transactional
    public EventDtoOut patchByInitiator(Long eventId, Long userId, EventDtoInInitiatorPatch eventDtoInInitiatorPatch) {

        LocalDateTime eventDate = eventDtoInInitiatorPatch.getEventDate();
        if (eventDtoInInitiatorPatch.getEventDate() != null) {
            checkEventDate(eventDate, LocalDateTime.now());
        }

        EventEntity eventEntity = findEventEntityByIdAndInitiatorId(eventId, userId);

        EventState actualEventStat = eventEntity.getState();
        if (!EventState.PENDING.equals(actualEventStat) && !EventState.CANCELED.equals(actualEventStat)) {
            throw new ConditionsNotMetException("Only pending or canceled events can be patched.");
        }
        EventStateAction requiredAction = eventDtoInInitiatorPatch.getStateAction();

        if (requiredAction != null) {
            switch (requiredAction) {
                case SEND_TO_REVIEW:
                    if (!EventState.CANCELED.equals(eventEntity.getState())) {
                        throw new ConditionsNotMetException("Only canceled events can be sent to review.");
                    }
                    eventEntity.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    if (!EventState.PENDING.equals(eventEntity.getState())) {
                        throw new ConditionsNotMetException("Review can be cancelled only for pending events.");
                    }
                    eventEntity.setState(EventState.CANCELED);
                    break;
                default:
                    throw new NotImplementedException("Action " + requiredAction + " is not implemented.");
            }
        }

        EventDtoOut eventDtoOut = returnPatchedEventDto(eventDtoInInitiatorPatch, eventEntity);
        log.debug("Patch event {} by initiator", eventDtoOut);
        return eventDtoOut;
    }

    @Override
    @Transactional
    public EventDtoOut patchByAdmin(Long eventId, EventDtoInAdminPatch eventDtoInAdminPatch) {

        LocalDateTime eventDate = eventDtoInAdminPatch.getEventDate();
        if (eventDtoInAdminPatch.getEventDate() != null) {
            checkEventDate(eventDate, LocalDateTime.now());
        }

        EventEntity eventEntity = findEventEntity(eventId);

        EventState actualEventStat = eventEntity.getState();

        if (!EventState.PENDING.equals(actualEventStat)) {
            throw new ConditionsNotMetException("Only pending events can be approved or rejected.");
        }

        EventStateAdminAction requiredAction = eventDtoInAdminPatch.getStateAction();

        switch (requiredAction) {
            case PUBLISH_EVENT:
                eventEntity.setState(EventState.PUBLISHED);
                eventEntity.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
                eventEntity.setState(EventState.CANCELED);
                break;
            default:
                throw new NotImplementedException("Action " + requiredAction + " is not implemented.");
        }

        EventDtoOut eventDtoOut = returnPatchedEventDto(eventDtoInAdminPatch, eventEntity);
        log.debug("Patch event {} by admin", eventDtoOut);
        return eventDtoOut;
    }

    @Override
    public List<EventDtoOut> findByFiltersAdmin(List<Long> users,
                                                List<EventState> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Integer from,
                                                Integer size) {

        BooleanBuilder filtersBuilder = new BooleanBuilder();

        if (users != null) {
            filtersBuilder.and(QEventEntity.eventEntity.initiator.id.in(users));
        }
        if (states != null) {
            filtersBuilder.and(QEventEntity.eventEntity.state.in(states));
        }
        if (categories != null) {
            filtersBuilder.and(QEventEntity.eventEntity.category.id.in(categories));
        }
        if (rangeStart != null) {
            filtersBuilder.and(QEventEntity.eventEntity.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            filtersBuilder.and(QEventEntity.eventEntity.eventDate.before(rangeEnd));
        }

        Iterable<EventEntity> eventsIterable = eventRepository.findAll(filtersBuilder);
        List<EventEntity> eventEntities =
                StreamSupport.stream(eventsIterable.spliterator(), false)
                        .collect(Collectors.toList());

        Map<Long, Integer> requestCountsByEventId = findConfirmedRequestsCountsByEventId(eventEntities);
        List<Long> eventIds = eventEntities.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> viewCountsByEventId = getViewsCountByEventIdFromStatistics(eventIds, UNIQUE_VIEWS);

        return eventDtoMapper.toDtoFull(
                eventEntities,
                requestCountsByEventId,
                viewCountsByEventId
        );
    }

    @Override
    public EventDtoOut findPublishedEventById(Long eventId, HttpServletRequest httpServletRequest) {
        EventEntity eventEntity = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event with id=" + eventId + " was not found"));
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = getViewsCountFromStatistics(eventId, UNIQUE_VIEWS);

        hitToStatistics(httpServletRequest);

        return eventDtoMapper.toDtoFull(eventEntity, confirmedRequests, views);
    }

    private void hitToStatistics(HttpServletRequest httpServletRequest) {
        try {
            statClient.saveHit(
                    StatDtoIn.builder()
                            .app("ewm-main")
                            .uri(httpServletRequest.getRequestURI())
                            .ip(httpServletRequest.getRemoteAddr())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        } catch (WebClientException e) {
            log.warn("Save endpoint hit by statistics client was not successful.", e);
        } catch (Throwable e) {
            log.warn("Unexpected error while saving endpoint by statistics client.", e);
        }
    }

    private Long getViewsCountFromStatistics(Long eventId, boolean unique) {
        String uri = "/events/" + eventId;
        List<StatDtoOut> stats;
        try {
            stats = statClient.getStats(
                    MIN_DATE,
                    MAX_DATE,
                    List.of(uri),
                    unique);
        } catch (WebClientException e) {
            log.warn("Get views count of uri " + uri + "by statistics client was not successful.", e);
            return 0L;
        } catch (Throwable e) {
            log.warn("Unexpected error while getting views count of uri " + uri + " by statistics client.", e);
            return 0L;
        }
        if (stats.isEmpty()) {
            return 0L;
        }
        if (stats.size() > 1 || !uri.equals(stats.get(0).getUri())) {
            log.warn("Strange response from statistics server. Requested uri=" + uri + ". Response: " + stats);
            return 0L;
        }
        return stats.get(0).getHits();
    }

    private Map<Long, Long> getViewsCountByEventIdFromStatistics(Collection<Long> eventIds, boolean unique) {

        List<String> uris = eventIds.stream()
                .map(eventId -> "/events/" + eventId)
                .collect(Collectors.toList());

        List<StatDtoOut> stats;
        try {
            stats = statClient.getStats(
                    MIN_DATE,
                    MAX_DATE,
                    uris,
                    unique);
        } catch (WebClientException e) {
            log.warn("Get views count of uris by statistics client was not successful.", e);
            return Map.of();
        } catch (Throwable e) {
            log.warn("Unexpected error while getting views count of uris by statistics client.", e);
            return Map.of();
        }

        try {
            return stats.stream()
                    .collect(Collectors.toMap(
                            statDtoOut -> Long.parseLong(
                                    statDtoOut.getUri().replaceFirst("/events/", "")),
                            StatDtoOut::getHits,
                            (hits1, hits2) -> hits1)
                    );
        } catch (Throwable e) {
            log.warn("Strange response from statistics server. Response: " + stats);
            return Map.of();
        }
    }

    private Map<Long, Integer> findConfirmedRequestsCountsByEventId(List<EventEntity> eventEntities) {
        List<Long> eventIds = eventEntities.stream().map(EventEntity::getId).collect(Collectors.toList());
        List<RequestRepository.CountById> requestsCount =
                requestRepository.countByEventIdInAndStatus(eventIds, RequestStatus.CONFIRMED);
        return requestsCount.stream()
                .collect(Collectors.toMap(
                        RequestRepository.CountById::getId,
                        RequestRepository.CountById::getCount
                ));
    }

    private EventDtoOut returnPatchedEventDto(EventDtoInPatch eventDtoInPatch, EventEntity eventEntity) {
        Long categoryId = eventDtoInPatch.getCategory();
        eventDtoMapper.updateByDto(
                eventEntity,
                eventDtoInPatch,
                categoryId == null ? null : findCategoryEntity(categoryId)
        );

        return eventDtoMapper.toDtoFull(eventEntity, null, null);
    }

    private static void checkEventDate(LocalDateTime eventDate, LocalDateTime now) {
        if (eventDate.minusHours(2).isBefore(now)) {
            throw new ConditionsNotMetException("Event date must be at least 2 hours in future.");
        }
    }

    private EventEntity findEventEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));
    }

    private EventEntity findEventEntityByIdAndInitiatorId(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " and initiator id=" + userId + " was not found."));
    }


    private CategoryEntity findCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found."));
    }

    private UserEntity findUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found."));
    }
}
