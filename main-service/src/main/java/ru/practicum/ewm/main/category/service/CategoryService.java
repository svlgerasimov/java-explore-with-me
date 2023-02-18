package ru.practicum.ewm.main.category.service;

import ru.practicum.ewm.main.category.dto.CategoryDtoIn;
import ru.practicum.ewm.main.category.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;

import java.util.List;

public interface CategoryService {
    CategoryDtoOut add(CategoryDtoIn categoryDtoIn);

    void delete(Long id);

    CategoryDtoOut patch(Long id, CategoryDtoInPatch categoryDtoInPatch);

    List<CategoryDtoOut> findAll(Integer from, Integer size);

    CategoryDtoOut findById(Long id);
}
