package ru.practicum.ewm.main.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoInPatch;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.main.compilations.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class CompilationController {

    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDtoOut post(@RequestBody @Valid CompilationDtoIn compilationDtoIn) {
        return compilationService.add(compilationDtoIn);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDtoOut patch(@PathVariable Long compId,
                                   @RequestBody @Valid CompilationDtoInPatch compilationDtoInPatch) {
        return compilationService.patch(compId, compilationDtoInPatch);
    }

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
