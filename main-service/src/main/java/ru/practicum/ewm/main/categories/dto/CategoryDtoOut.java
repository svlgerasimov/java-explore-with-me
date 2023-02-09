package ru.practicum.ewm.main.categories.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CategoryDtoOut {
    Long id;
    String name;
}
