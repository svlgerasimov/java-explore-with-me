package ru.practicum.ewm.stats.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@RequiredArgsConstructor
@Jacksonized
public class StatDtoOut {
    String app;
    String uri;
    Long hits;
}
