package ru.practicum.ewm.stats.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class StatDtoOut {
    String app;
    String uri;
    Long hits;
}
