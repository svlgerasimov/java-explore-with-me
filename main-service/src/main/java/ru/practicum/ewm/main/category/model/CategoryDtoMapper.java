package ru.practicum.ewm.main.category.model;

import org.mapstruct.*;
import ru.practicum.ewm.main.category.dto.CategoryDtoIn;
import ru.practicum.ewm.main.category.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper {

    @Mapping(target = "id", ignore = true)
    CategoryEntity fromDto(CategoryDtoIn categoryDtoIn);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateByDto(@MappingTarget CategoryEntity categoryEntity,
                     CategoryDtoInPatch categoryDtoInPatch);

    CategoryDtoOut toDto(CategoryEntity categoryEntity);

    List<CategoryDtoOut> toDto(List<CategoryEntity> categoryEntities);
}
