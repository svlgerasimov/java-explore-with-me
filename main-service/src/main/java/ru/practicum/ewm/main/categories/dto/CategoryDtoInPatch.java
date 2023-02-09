package ru.practicum.ewm.main.categories.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.main.util.validation.NullableNotBlank;

@Value
@Builder
@Jacksonized
public class CategoryDtoInPatch {

    @NullableNotBlank
    String name;
}
