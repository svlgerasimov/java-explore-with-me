package ru.practicum.ewm.main.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoInPatch;
import ru.practicum.ewm.main.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.main.compilations.model.CompilationDtoMapper;
import ru.practicum.ewm.main.compilations.model.CompilationEntity;
import ru.practicum.ewm.main.compilations.model.CompilationEventEntity;
import ru.practicum.ewm.main.compilations.repository.CompilationEventRepository;
import ru.practicum.ewm.main.compilations.repository.CompilationRepository;
import ru.practicum.ewm.main.event.repository.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventRepository eventRepository;

    private final CompilationDtoMapper compilationDtoMapper;

    @Override
    @Transactional
    public CompilationDtoOut add(CompilationDtoIn compilationDtoIn) {
        CompilationEntity inputCompilationEntity = compilationDtoMapper.createFromDto(compilationDtoIn);
        CompilationEntity compilationEntity = compilationRepository.save(inputCompilationEntity);
        Set<Long> eventIds = compilationDtoIn.getEvents();

        return null;
    }

    @Override
    @Transactional
    public void delete(Long id) {

    }

    @Override
    @Transactional
    public CompilationDtoOut patch(CompilationDtoInPatch compilationDtoInPatch) {
        return null;
    }

    @Override
    public List<CompilationDtoOut> findAll(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public CompilationDtoOut findById(Long compId) {
        return null;
    }
}
