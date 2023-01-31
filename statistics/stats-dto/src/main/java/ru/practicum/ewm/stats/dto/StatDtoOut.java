package ru.practicum.ewm.stats.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StatDtoOut {
    String app;
    String uri;
    Long hits;
}
