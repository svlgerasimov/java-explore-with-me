package ru.practicum.ewm.stats.repository;

import ru.practicum.ewm.stats.dto.StatDtoOut;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsCustomRepository {
    List<StatDtoOut> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
