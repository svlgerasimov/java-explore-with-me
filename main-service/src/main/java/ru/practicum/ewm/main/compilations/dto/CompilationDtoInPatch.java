package ru.practicum.ewm.main.compilations.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.main.util.validation.NullableNotBlank;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class CompilationDtoInPatch {

    Set<Long> events;

    Boolean pinned;

    @NullableNotBlank
    String title;
}
