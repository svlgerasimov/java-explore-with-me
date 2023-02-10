package ru.practicum.ewm.main.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.categories.dto.CategoryDtoIn;
import ru.practicum.ewm.main.categories.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.categories.dto.CategoryDtoOut;
import ru.practicum.ewm.main.categories.model.CategoryDtoMapper;
import ru.practicum.ewm.main.categories.model.CategoryEntity;
import ru.practicum.ewm.main.categories.repository.CategoryRepository;
import ru.practicum.ewm.main.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDtoMapper categoryDtoMapper;

    @Override
    @Transactional
    public CategoryDtoOut add(CategoryDtoIn categoryDtoIn) {
        CategoryEntity categoryEntity = categoryDtoMapper.fromDto(categoryDtoIn);
        categoryEntity = categoryRepository.save(categoryEntity);
        CategoryDtoOut categoryDtoOut = categoryDtoMapper.toDto(categoryEntity);
        log.debug("Add category {}", categoryDtoOut);
        return categoryDtoOut;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findCategoryEntity(id);
        log.debug("Delete category with id={}", id);
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDtoOut patch(Long id, CategoryDtoInPatch categoryDtoInPatch) {
        CategoryEntity categoryEntity = findCategoryEntity(id);
        categoryDtoMapper.updateByDto(categoryEntity, categoryDtoInPatch);
        categoryRepository.save(categoryEntity);
        CategoryDtoOut categoryDtoOut = categoryDtoMapper.toDto(categoryEntity);
        log.debug("Update category {}", categoryDtoOut);
        return categoryDtoOut;
    }

    @Override
    public List<CategoryDtoOut> findAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<CategoryEntity> entities = categoryRepository.findAll(pageable).getContent();
        return categoryDtoMapper.toDto(entities);
    }

    @Override
    public CategoryDtoOut findById(Long id) {
        CategoryEntity entity = findCategoryEntity(id);
        return categoryDtoMapper.toDto(entity);
    }

    private CategoryEntity findCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found."));
    }
}
