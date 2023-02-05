package ru.practicum.ewm.stats.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.stats.dto.StatDtoIn;

@Mapper(componentModel = "spring")
public interface StatDtoInMapper {

    @Mapping(target = "id", ignore = true)
    StatEntity fromDto(StatDtoIn dto);
}
