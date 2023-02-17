package ru.practicum.ewm.main.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.category.dto.CategoryDtoIn;
import ru.practicum.ewm.main.category.dto.CategoryDtoInPatch;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;
import ru.practicum.ewm.main.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CategoryAdminController {

    private final CategoryService categoryService;

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
}
