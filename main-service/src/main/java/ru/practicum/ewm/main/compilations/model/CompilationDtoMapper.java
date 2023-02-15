package ru.practicum.ewm.main.compilations.model;

import org.mapstruct.*;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoInPatch;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.main.event.dto.EventDtoOutShort;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CompilationDtoMapper {

    CompilationEntity createFromDto(CompilationDtoIn compilationDtoIn);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateByDto(@MappingTarget CompilationEntity compilationEntity,
                     CompilationDtoInPatch compilationDtoInPatch);

    CompilationDtoOut toDto(CompilationEntity compilationEntity, List<EventDtoOutShort> events);

    @Named("toDto")
    default List<CompilationDtoOut> toDto(List<CompilationEntity> compilationEntities,
                                          Map<Long, List<EventDtoOutShort>> eventsByCompId) {
        return compilationEntities.stream()
                .map(compilationEntity -> {
                    Long compId = compilationEntity.getId();
                    List<EventDtoOutShort> events = eventsByCompId.get(compId);
                    return toDto(
                            compilationEntity,
                            events == null ? Collections.emptyList() : events
                    );
                })
                .collect(Collectors.toList());
    }
}
