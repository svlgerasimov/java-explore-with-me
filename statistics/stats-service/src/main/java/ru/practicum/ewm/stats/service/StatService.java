package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void save(StatDtoIn statDtoIn);
    List<StatDtoOut> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
