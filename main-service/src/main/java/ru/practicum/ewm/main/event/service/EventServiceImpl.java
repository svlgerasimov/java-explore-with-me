package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.event.dto.EventDtoIn;
import ru.practicum.ewm.main.event.dto.EventDtoInPatch;
import ru.practicum.ewm.main.event.dto.EventDtoOut;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;
import ru.practicum.ewm.main.event.model.EventDtoMapper;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.model.UserEntity;
import ru.practicum.ewm.main.user.repository.UserRepository;
import ru.practicum.ewm.stats.client.StatClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventDtoMapper eventDtoMapper;
    private final StatClient statClient;

    @Override
    @Transactional
    public EventDtoOut add(Long userId, EventDtoIn eventDtoIn) {
        LocalDateTime eventDate = eventDtoIn.getEventDate();
        LocalDateTime now = LocalDateTime.now();
        if (eventDate.minusHours(2).isBefore(now)) {
            throw new ConditionsNotMetException("Event date must be at least 2 hours in future.");
        }
        UserEntity initiator = findUserEntity(userId);
        CategoryEntity category = findCategoryEntity(eventDtoIn.getCategory());
        EventEntity eventEntity = eventDtoMapper.createFromDto(eventDtoIn, initiator, category, now);
        eventEntity = eventRepository.save(eventEntity);
        EventDtoOut eventDtoOut = eventDtoMapper.toDtoFull(eventEntity, null, null);
        log.debug("Add event {}", eventDtoOut);
        return null;
    }

    @Override
    public List<EventDtoOutShort> findAllByInitiatorId(Long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventDtoOut findByEventIdAndInitiatorId(Long eventId, Long userId) {
        EventEntity eventEntity = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " and initiator id=" + userId + " was not found."));
        // TODO реализовать добавление количества просмотров и подтвержденных запросов
        return eventDtoMapper.toDtoFull(eventEntity, 0, 0L);
    }

    @Override
    public EventDtoOut patch(Long eventId, Long userId, EventDtoInPatch eventDtoInPatch) {
        return null;
    }

    private EventEntity findEventEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));
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
