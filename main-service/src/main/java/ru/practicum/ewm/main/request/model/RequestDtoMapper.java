package ru.practicum.ewm.main.request.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;

@Mapper(componentModel = "spring")
public interface RequestDtoMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDtoOut toDto(RequestEntity requestEntity);
}
