package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;
import ru.practicum.ewm.stats.model.StatDtoInMapper;
import ru.practicum.ewm.stats.model.StatEntity;
import ru.practicum.ewm.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private final StatsRepository statsRepository;
    private final StatDtoInMapper statDtoInMapper;

    @Override
    @Transactional
    public void save(StatDtoIn statDtoIn) {
        StatEntity statEntity = statDtoInMapper.fromDto(statDtoIn);
        statsRepository.save(statEntity);
        log.debug("Save statistics hit: " + statEntity);
    }

    @Override
    public List<StatDtoOut> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return statsRepository.get(start, end, uris, unique);
    }
}
