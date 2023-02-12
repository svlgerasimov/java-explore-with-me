package ru.practicum.ewm.main.user.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.main.user.dto.UserDtoIn;
import ru.practicum.ewm.main.user.dto.UserDtoOut;
import ru.practicum.ewm.main.user.dto.UserDtoOutShort;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    @Mapping(target = "id", ignore = true)
    UserEntity fromDto(UserDtoIn userDtoIn);

    UserDtoOut toDto(UserEntity userEntity);

    List<UserDtoOut> toDto(List<UserEntity> userEntities);

    UserDtoOutShort toDtoShort(UserEntity userEntity);
}
