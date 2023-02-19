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
import ru.practicum.ewm.main.event.model.ReviewEntity;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.event.repository.ReviewRepository;
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
import java.util.Optional;
import java.util.function.Function;
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
    private final ReviewRepository reviewRepository;
    private final EventDtoMapper eventDtoMapper;
    private final StatisticsService statisticsService;

    @Override
    @Transactional
    public EventDtoOutPrivate add(Long userId, EventDtoIn eventDtoIn) {
        LocalDateTime eventDate = eventDtoIn.getEventDate();
        LocalDateTime now = LocalDateTime.now();
        checkEventDate(eventDate, now);
        UserEntity initiator = findUserEntity(userId);
        CategoryEntity category = findCategoryEntity(eventDtoIn.getCategory());
        EventEntity eventEntity = eventDtoMapper.createFromDto(eventDtoIn, initiator, category, now);
        eventEntity = eventRepository.save(eventEntity);

        EventDtoOutPrivate eventDtoOut = eventDtoMapper.toDtoPrivate(eventEntity,
                null, null, null);
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
    public EventDtoOutPrivate findByEventIdAndInitiatorId(Long eventId, Long userId) {
        EventEntity eventEntity = findEventEntityByIdAndInitiatorId(eventId, userId);
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = statisticsService.getViewsCountFromStatistics(eventId);

        // Если при последней модерации событие было отклонено и был оставлен комментарий,
        // то он будет прикреплен к dto
        ReviewEntity reviewEntity = findActualRejectReview(eventEntity).orElse(null);

        return eventDtoMapper.toDtoPrivate(eventEntity, confirmedRequests, views, reviewEntity);
    }

    @Override
    @Transactional
    public EventDtoOutPrivate patchByInitiator(Long eventId, Long userId,
                                               EventDtoInInitiatorPatch eventDtoInInitiatorPatch) {

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

        EventDtoOutPrivate eventDtoOut = returnPatchedEventDto(eventDtoInInitiatorPatch,
                eventEntity, null);
        log.debug("Patch event {} by initiator", eventDtoOut);
        return eventDtoOut;
    }

    @Override
    @Transactional
    public EventDtoOutPrivate patchByAdmin(Long eventId, EventDtoInAdminPatch eventDtoInAdminPatch) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime eventDate = eventDtoInAdminPatch.getEventDate();
        if (eventDtoInAdminPatch.getEventDate() != null) {
            checkEventDate(eventDate, now);
        }

        EventEntity eventEntity = findEventEntity(eventId);

        EventState actualEventStat = eventEntity.getState();

        if (!EventState.PENDING.equals(actualEventStat)) {
            throw new ConditionsNotMetException("Only pending events can be approved or rejected.");
        }

        EventStateAdminAction requiredAction = eventDtoInAdminPatch.getStateAction();
        ReviewEntity reviewEntity = null;

        if (requiredAction != null) {
            reviewEntity = new ReviewEntity();
            reviewEntity.setEvent(eventEntity);
            reviewEntity.setCreatedOn(now);
            String rejectionComment = eventDtoInAdminPatch.getRejectionComment();
            switch (requiredAction) {
                case PUBLISH_EVENT:
                    if (rejectionComment != null) {
                        throw new ConditionsNotMetException("Only rejection review can contain a comment.");
                    }
                    eventEntity.setState(EventState.PUBLISHED);
                    eventEntity.setPublishedOn(now);
                    reviewEntity.setAction(EventStateAdminAction.PUBLISH_EVENT);
                    break;
                case REJECT_EVENT:
                    eventEntity.setState(EventState.CANCELED);
                    reviewEntity.setAction(EventStateAdminAction.REJECT_EVENT);
                    reviewEntity.setComment(rejectionComment);
                    break;
                default:
                    throw new NotImplementedException("Action " + requiredAction + " is not implemented.");
            }
            reviewEntity = reviewRepository.save(reviewEntity);
        }

        EventDtoOutPrivate eventDtoOut = returnPatchedEventDto(eventDtoInAdminPatch, eventEntity, reviewEntity);
        log.debug("Patch event {} by admin", eventDtoOut);
        return eventDtoOut;
    }

    @Override
    public List<EventDtoOutPrivate> findByFiltersAdmin(List<Long> users,
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

        return getEventDtoOutPrivates(eventEntities);
    }

    @Override
    public List<EventDtoOutPrivate> findAllPending(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventEntity> eventEntities = eventRepository.findAllByState(EventState.PENDING, pageable);
        return getEventDtoOutPrivates(eventEntities);
    }

    @Override
    public EventDtoOutPublic findPublishedEventById(Long eventId, HttpServletRequest httpServletRequest) {
        EventEntity eventEntity = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event with id=" + eventId + " was not found"));
        int confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = statisticsService.getViewsCountFromStatistics(eventId);

        statisticsService.hitToStatistics(httpServletRequest);

        return eventDtoMapper.toDtoPublic(eventEntity, confirmedRequests, views);
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

    private EventDtoOutPrivate returnPatchedEventDto(EventDtoInPatch eventDtoInPatch,
                                                     EventEntity eventEntity,
                                                     ReviewEntity actualRejectionReview) {
        Long categoryId = eventDtoInPatch.getCategory();
        eventDtoMapper.updateByDto(
                eventEntity,
                eventDtoInPatch,
                categoryId == null ? null : findCategoryEntity(categoryId)
        );

        return eventDtoMapper.toDtoPrivate(eventEntity, null, null, actualRejectionReview);
    }

    private static void checkEventDate(LocalDateTime eventDate, LocalDateTime now) {
        if (eventDate.minusHours(2).isBefore(now)) {
            throw new ConditionsNotMetException("Event date must be at least 2 hours in future.");
        }
    }

    // Если при последней модерации событие было отклонено и был оставлен комментарий,
    // возвращает Optional данных этой модерации
    private Optional<ReviewEntity> findActualRejectReview(EventEntity eventEntity) {
        return reviewRepository.findFirstByEventOrderByCreatedOnDesc(eventEntity)
                .filter(review ->
                        EventStateAdminAction.REJECT_EVENT.equals(review.getAction())
                                && review.getComment() != null);
    }

    // Строит соответствие id событий с данными последней модерации события при условии,
    // что событие было отклонено и был оставлен комментарий
    private Map<Long, ReviewEntity> findActualRejectReviewByEvent(List<Long> eventIds) {
        return reviewRepository.findAllByEventIdIn(eventIds).stream()
                .collect(Collectors.toMap(
                        reviewEntity -> reviewEntity.getEvent().getId(),
                        Function.identity(),
                        (review1, review2) ->
                                review2.getCreatedOn().isAfter(review1.getCreatedOn()) ? review2 : review1))
                .entrySet().stream()
                .filter(entry ->
                        EventStateAdminAction.REJECT_EVENT.equals(entry.getValue().getAction())
                                && entry.getValue().getComment() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<EventDtoOutPrivate> getEventDtoOutPrivates(List<EventEntity> eventEntities) {
        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEvent(eventEntities);
        List<Long> eventIds = eventEntities.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(eventIds);
        Map<Long, ReviewEntity> actualRejectReviewByEventId = findActualRejectReviewByEvent(eventIds);

        return eventDtoMapper.toDtoPrivate(
                eventEntities,
                requestCountsByEventId,
                viewCountsByEventId,
                actualRejectReviewByEventId
        );
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
