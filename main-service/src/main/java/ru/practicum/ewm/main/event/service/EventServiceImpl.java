package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.EventDtoMapper;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.NotImplementedException;
import ru.practicum.ewm.main.request.dto.RequestStatus;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.statistics.StatisticsService;
import ru.practicum.ewm.main.user.model.UserEntity;
import ru.practicum.ewm.main.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final StatisticsService statisticsService;

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

        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEvent(eventEntities);
        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(eventIds);

        return eventDtoMapper.toDtoShort(eventEntities, requestCountsByEventId, viewCountsByEventId);
    }

    @Override
    public EventDtoOut findByEventIdAndInitiatorId(Long eventId, Long userId) {
        EventEntity eventEntity = findEventEntityByIdAndInitiatorId(eventId, userId);
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = statisticsService.getViewsCountFromStatistics(eventId);
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

        List<EventEntity> eventEntities = eventRepository.findByFiltersAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );

        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEvent(eventEntities);
        List<Long> eventIds = eventEntities.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(eventIds);

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
        long views = statisticsService.getViewsCountFromStatistics(eventId);

        statisticsService.hitToStatistics(httpServletRequest);

        return eventDtoMapper.toDtoFull(eventEntity, confirmedRequests, views);
    }

    @Override
    public List<EventDtoOutShort> findPublishedEventsByFilters(String text, List<Long> categories, Boolean paid,
                                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                               Boolean onlyAvailable, EventSortType sortType,
                                                               Integer from, Integer size,
                                                               HttpServletRequest httpServletRequest) {
        List<EventDtoOutShort> eventDtos;
        switch (sortType) {
            case EVENT_DATE:
                eventDtos = findPublishedEventsByFiltersOrderByDate(text, categories, paid,
                        rangeStart, rangeEnd, onlyAvailable, from, size);
                break;
            case VIEWS:
                eventDtos = findPublishedEventsByFiltersOrderByViews(text, categories, paid,
                        rangeStart, rangeEnd, onlyAvailable, from, size);
                break;
            default:
                throw new NotImplementedException("Sort type " + sortType + " is not implemented.");
        }
        statisticsService.hitToStatistics(httpServletRequest);
        return eventDtos;
    }

    private List<EventDtoOutShort> findPublishedEventsByFiltersOrderByViews(String text, List<Long> categories,
                                                                            Boolean paid, LocalDateTime rangeStart,
                                                                            LocalDateTime rangeEnd,
                                                                            Boolean onlyAvailable,
                                                                            Integer from, Integer size) {
        List<Long> eventIds = eventRepository.findIdsOfPublishedEventsByFilters(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(eventIds);

        List<Long> limitedIds = eventIds.stream()
                .sorted(Comparator.comparingLong(id -> viewCountsByEventId.getOrDefault(id, 0L))
                        .reversed())
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        List<EventEntity> eventEntities = eventRepository.findAllByIdIn(limitedIds).stream()
                .sorted(Comparator.<EventEntity>comparingLong(eventEntity ->
                        viewCountsByEventId.getOrDefault(eventEntity.getId(), 0L)).reversed())
                .collect(Collectors.toList());
        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEvent(eventEntities);
        return eventDtoMapper.toDtoShort(
                eventEntities,
                requestCountsByEventId,
                viewCountsByEventId);
    }

    private List<EventDtoOutShort> findPublishedEventsByFiltersOrderByDate(String text,
                                                                           List<Long> categories,
                                                                           Boolean paid,
                                                                           LocalDateTime rangeStart,
                                                                           LocalDateTime rangeEnd,
                                                                           Boolean onlyAvailable,
                                                                           Integer from,
                                                                           Integer size) {
        List<EventEntity> eventEntities = eventRepository.findPublishedEventsByFiltersOrderByDate(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);

        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEvent(eventEntities);
        List<Long> eventIds = eventEntities.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(eventIds);

        return eventDtoMapper.toDtoShort(
                eventEntities,
                requestCountsByEventId,
                viewCountsByEventId);
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
