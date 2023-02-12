package ru.practicum.ewm.main.event.model;

import org.mapstruct.*;
import ru.practicum.ewm.main.category.model.CategoryDtoMapper;
import ru.practicum.ewm.main.category.model.CategoryEntity;
import ru.practicum.ewm.main.event.dto.EventDtoIn;
import ru.practicum.ewm.main.event.dto.EventDtoInPatch;
import ru.practicum.ewm.main.event.dto.EventDtoOut;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;
import ru.practicum.ewm.main.user.model.UserDtoMapper;
import ru.practicum.ewm.main.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {UserDtoMapper.class,
                CategoryDtoMapper.class,
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

    EventDtoOut toDtoFull(EventEntity eventEntity, Integer confirmedRequests, Long views);

    EventDtoOutShort toDtoShort(EventEntity eventEntity, Integer confirmedRequests, Long views);

    @Named("toDtoShort")
    default List<EventDtoOutShort> toDtoShort(List<EventEntity> eventEntities,
                                              Map<Long, Integer> confirmedRequestsByEventId,
                                              Map<Long, Long> viewsByEventId) {
        return eventEntities.stream()
                .map(eventEntity -> {
                    Long eventId = eventEntity.getId();
                    Integer confirmedRequests = confirmedRequestsByEventId.get(eventId);
                    Long views = viewsByEventId.get(eventId);
                    return toDtoShort(eventEntity,
                            confirmedRequests == null ? 0 : confirmedRequests,
                            views == null ? 0 : views);
                })
                .collect(Collectors.toList());
    }
}
