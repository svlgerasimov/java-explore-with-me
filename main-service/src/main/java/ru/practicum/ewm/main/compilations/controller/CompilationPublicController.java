package ru.practicum.ewm.main.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.main.compilations.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping("/compilations")
    public List<CompilationDtoOut> findAll(@RequestParam(defaultValue = "false") Boolean pinned,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        return compilationService.findAll(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDtoOut findById(@PathVariable Long compId) {
        return compilationService.findById(compId);
    }
}
