package ru.practicum.ewm.main.event.model;

import org.mapstruct.Mapper;
import ru.practicum.ewm.main.event.dto.ReviewDtoOut;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDtoOut toDto(ReviewEntity reviewEntity);
}
