package ru.practicum.ewm.stats.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {
    @Override
    public void save(StatDtoIn statDtoIn) {

    }

    @Override
    public List<StatDtoOut> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return null;
    }
}
