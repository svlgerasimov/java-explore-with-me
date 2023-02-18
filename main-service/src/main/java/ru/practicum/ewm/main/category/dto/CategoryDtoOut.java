package ru.practicum.ewm.main.category.dto;

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
