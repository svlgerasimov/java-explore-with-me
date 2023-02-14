package ru.practicum.ewm.main.request.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;
import ru.practicum.ewm.main.request.dto.RequestStatusUpdateDtoOut;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestDtoMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDtoOut toDto(RequestEntity requestEntity);

    List<RequestDtoOut> toDto(List<RequestEntity> requestEntities);

    RequestStatusUpdateDtoOut toUpdateDto(List<RequestEntity> confirmedRequests,
                                          List<RequestEntity> rejectedRequests);
}
