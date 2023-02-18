package ru.practicum.ewm.main.compilations.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoInPatch;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.main.compilations.model.CompilationDtoMapper;
import ru.practicum.ewm.main.compilations.model.CompilationEntity;
import ru.practicum.ewm.main.compilations.model.CompilationEventEntity;
import ru.practicum.ewm.main.compilations.model.QCompilationEntity;
import ru.practicum.ewm.main.compilations.repository.CompilationEventRepository;
import ru.practicum.ewm.main.compilations.repository.CompilationRepository;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;
import ru.practicum.ewm.main.event.model.EventDtoMapper;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.statistics.StatisticsService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatisticsService statisticsService;

    private final CompilationDtoMapper compilationDtoMapper;
    private final EventDtoMapper eventDtoMapper;

    @Override
    @Transactional
    public CompilationDtoOut add(CompilationDtoIn compilationDtoIn) {
        CompilationEntity compilationEntity = compilationRepository.save(
                compilationDtoMapper.createFromDto(compilationDtoIn)
        );
        Set<Long> eventIds = compilationDtoIn.getEvents();
        List<EventDtoOutShort> eventDtoOutShorts = saveCompilationEventEntities(compilationEntity, eventIds);
        CompilationDtoOut compilationDtoOut = compilationDtoMapper.toDto(compilationEntity, eventDtoOutShorts);

        log.debug("Add compilation {}", compilationDtoOut);
        return compilationDtoOut;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CompilationEntity compilationEntity = findCompilationEntity(id);
        log.debug("Delete compilation with id={}", id);
        compilationEventRepository.deleteAllByCompilation(compilationEntity);
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDtoOut patch(Long compId, CompilationDtoInPatch compilationDtoInPatch) {
        CompilationEntity compilationEntity = findCompilationEntity(compId);
        compilationDtoMapper.updateByDto(compilationEntity, compilationDtoInPatch);

        List<EventDtoOutShort> eventDtoOutShorts;
        Set<Long> eventIds = compilationDtoInPatch.getEvents();
        if (eventIds != null) {
            compilationEventRepository.deleteAllByCompilation(compilationEntity);
            eventDtoOutShorts = saveCompilationEventEntities(compilationEntity, eventIds);
        } else {
            eventDtoOutShorts = Collections.emptyList();
        }

        CompilationDtoOut compilationDtoOut = compilationDtoMapper.toDto(compilationEntity, eventDtoOutShorts);

        log.debug("Patch compilation {}", compilationDtoOut);
        return compilationDtoOut;
    }

    @Override
    public List<CompilationDtoOut> findAll(Boolean pinned, Integer from, Integer size) {
        BooleanBuilder filtersBuilder = new BooleanBuilder();
        if (pinned) {
            filtersBuilder.and(QCompilationEntity.compilationEntity.pinned.isTrue());
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<CompilationEntity> compilationEntities =
                compilationRepository.findAll(filtersBuilder, pageable).toList();

        List<Long> compilationIds = compilationEntities.stream()
                .map(CompilationEntity::getId)
                .collect(Collectors.toList());

        Map<Long, List<Long>> eventIdsByCompilationIds =
                compilationEventRepository.findEventsByCompilationIdsIn(compilationIds).stream()
                .collect(Collectors.groupingBy(
                        CompilationEventRepository.EventsIdsByCompilationId::getCompId,
                        HashMap::new,
                        Collectors.mapping(CompilationEventRepository.EventsIdsByCompilationId::getEventId,
                                Collectors.toList())
                ));

        List<Long> uniqueEventIds = eventIdsByCompilationIds.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        List<EventEntity> allEventEntities = eventRepository.findAllByIdIn(uniqueEventIds);
        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEventId(uniqueEventIds);
        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(uniqueEventIds);
        List<EventDtoOutShort> allEventDtoOutShorts =
                eventDtoMapper.toDtoShort(allEventEntities, requestCountsByEventId, viewCountsByEventId);

        Map<Long, EventDtoOutShort> eventsDtoById = allEventDtoOutShorts.stream()
                .collect(Collectors.toMap(EventDtoOutShort::getId, Function.identity()));
        Map<Long, List<EventDtoOutShort>> evensDtoByCompId =
                eventIdsByCompilationIds.entrySet().stream()
                        .map(
                                entry -> Map.entry(entry.getKey(),
                                entry.getValue().stream().map(eventsDtoById::get).collect(Collectors.toList()))
                        )
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return compilationDtoMapper.toDto(compilationEntities, evensDtoByCompId);
    }

    @Override
    public CompilationDtoOut findById(Long compId) {
        CompilationEntity compilationEntity = findCompilationEntity(compId);
        List<EventEntity> eventEntities = compilationEventRepository.findEventsByCompilation(compilationEntity);
        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEvent(eventEntities);
        List<Long> eventIds = eventEntities.stream()
                .map(EventEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(eventIds);
        List<EventDtoOutShort> eventDtoOutShorts =
                eventDtoMapper.toDtoShort(eventEntities, requestCountsByEventId, viewCountsByEventId);

        return compilationDtoMapper.toDto(compilationEntity, eventDtoOutShorts);
    }

    private List<EventDtoOutShort> saveCompilationEventEntities(CompilationEntity compilationEntity,
                                                                Set<Long> eventIds) {
        List<EventEntity> eventEntities = eventRepository.findAllByIdIn(eventIds);
        List<CompilationEventEntity> compilationEventEntities = eventEntities.stream()
                .map(eventEntity -> {
                    CompilationEventEntity compilationEventEntity = new CompilationEventEntity();
                    compilationEventEntity.setCompilation(compilationEntity);
                    compilationEventEntity.setEvent(eventEntity);
                    return compilationEventEntity;
                })
                .collect(Collectors.toList());
        compilationEventRepository.saveAll(compilationEventEntities);

        Map<Long, Integer> requestCountsByEventId =
                requestRepository.findConfirmedRequestsCountsByEvent(eventEntities);
        Map<Long, Long> viewCountsByEventId =
                statisticsService.getViewsCountByEventIdFromStatistics(eventIds);

        return eventDtoMapper.toDtoShort(
                eventEntities, requestCountsByEventId, viewCountsByEventId);
    }

    private CompilationEntity findCompilationEntity(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found."));
    }

    private EventEntity findEventEntity(Long id) {

        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));
    }
}
