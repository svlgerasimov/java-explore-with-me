package ru.practicum.ewm.main.compilations.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoInPatch;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoOut;

import java.util.List;

public interface CompilationService {
    @Transactional
    CompilationDtoOut add(CompilationDtoIn compilationDtoIn);

    @Transactional
    void delete(Long id);

    @Transactional
    CompilationDtoOut patch(CompilationDtoInPatch compilationDtoInPatch);

    List<CompilationDtoOut> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDtoOut findById(Long compId);
}
