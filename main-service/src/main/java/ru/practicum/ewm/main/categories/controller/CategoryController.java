package ru.practicum.ewm.main.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.categories.dto.CategoryDtoIn;
import ru.practicum.ewm.main.categories.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.categories.dto.CategoryDtoOut;
import ru.practicum.ewm.main.categories.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    // Admin API

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDtoOut post(@Valid @RequestBody CategoryDtoIn categoryDtoIn) {
        return categoryService.add(categoryDtoIn);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDtoOut patch(@PathVariable Long catId,
                      @Valid @RequestBody CategoryDtoInPatch categoryDtoInPatch) {
        return categoryService.patch(catId, categoryDtoInPatch);
    }

    // Public API

    @GetMapping("/categories")
    public List<CategoryDtoOut> findAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoryService.findAll(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDtoOut findById(@PathVariable Long catId) {
        return categoryService.findById(catId);
    }
}
