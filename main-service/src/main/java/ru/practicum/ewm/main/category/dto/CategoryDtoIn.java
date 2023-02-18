package ru.practicum.ewm.main.category.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Value
@Builder
@Jacksonized
public class CategoryDtoIn {
    @NotBlank
    String name;
}
