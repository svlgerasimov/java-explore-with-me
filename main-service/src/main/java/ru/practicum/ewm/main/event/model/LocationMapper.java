package ru.practicum.ewm.main.event.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.main.event.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "longitude", source = "lon")
    @Mapping(target = "latitude", source = "lat")
    LocationModel fromDto(LocationDto locationDto);

    @Mapping(target = "lon", source = "longitude")
    @Mapping(target = "lat", source = "latitude")
    LocationDto toDto(LocationModel locationModel);
}
