package ru.practicum.ewm.main.event.model;

import org.mapstruct.*;
import ru.practicum.ewm.main.category.model.CategoryDtoMapper;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.user.model.UserDtoMapper;
import ru.practicum.ewm.main.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {UserDtoMapper.class,
                CategoryDtoMapper.class,
                ReviewMapper.class,
                LocationMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", source = "categoryEntity")
    @Mapping(target = "initiator", source = "initiatorEntity")
    EventEntity createFromDto(EventDtoIn eventDtoIn,
                              UserEntity initiatorEntity,
                              CategoryEntity categoryEntity,
                              LocalDateTime createdOn);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "category", source = "categoryEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateByDto(@MappingTarget EventEntity eventEntity,
                     EventDtoInPatch eventDtoInPatch,
                     CategoryEntity categoryEntity);

    EventDtoOutPublic toDtoPublic(EventEntity eventEntity, Integer confirmedRequests, Long views);

    @Mapping(target = "id", source = "eventEntity.id")
    @Mapping(target = "createdOn", source = "eventEntity.createdOn")
    EventDtoOutPrivate toDtoPrivate(EventEntity eventEntity,
                                    Integer confirmedRequests, Long views, ReviewEntity actualRejectionReview);

    @Named("toDtoPublic")
    default List<EventDtoOutPublic> toDtoPublic(List<EventEntity> eventEntities,
                                                Map<Long, Integer> confirmedRequestsByEventId,
                                                Map<Long, Long> viewsByEventId) {
        return eventEntities.stream()
                .map(eventEntity -> {
                    Long eventId = eventEntity.getId();
                    return toDtoPublic(eventEntity,
                            confirmedRequestsByEventId.getOrDefault(eventId, 0),
                            viewsByEventId.getOrDefault(eventId, 0L));
                })
                .collect(Collectors.toList());
    }

    @Named("toDtoPrivate")
    default List<EventDtoOutPrivate> toDtoPrivate(List<EventEntity> eventEntities,
                                                  Map<Long, Integer> confirmedRequestsByEventId,
                                                  Map<Long, Long> viewsByEventId,
                                                  Map<Long, ReviewEntity> actualRejectionReviewByEventId) {
        return eventEntities.stream()
                .map(eventEntity -> {
                    Long eventId = eventEntity.getId();
                    return toDtoPrivate(eventEntity,
                            confirmedRequestsByEventId.getOrDefault(eventId, 0),
                            viewsByEventId.getOrDefault(eventId, 0L),
                            actualRejectionReviewByEventId.get(eventId));
                })
                .collect(Collectors.toList());
    }

    EventDtoOutShort toDtoShort(EventEntity eventEntity, Integer confirmedRequests, Long views);

    @Named("toDtoShort")
    default List<EventDtoOutShort> toDtoShort(List<EventEntity> eventEntities,
                                              Map<Long, Integer> confirmedRequestsByEventId,
                                              Map<Long, Long> viewsByEventId) {
        return eventEntities.stream()
                .map(eventEntity -> {
                    Long eventId = eventEntity.getId();
                    return toDtoShort(eventEntity,
                            confirmedRequestsByEventId.getOrDefault(eventId, 0),
                            viewsByEventId.getOrDefault(eventId, 0L));
                })
                .collect(Collectors.toList());
    }
}
