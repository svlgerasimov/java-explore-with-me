package ru.practicum.ewm.main.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.category.dto.CategoryDtoOut;
import ru.practicum.ewm.main.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class CategoryPublicController {

    private final CategoryService categoryService;

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
