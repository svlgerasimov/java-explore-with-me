package ru.practicum.ewm.main.compilations.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class CompilationDtoIn {

    @Builder.Default
    Set<Long> events = Set.of();

    @Builder.Default
    Boolean pinned = false;

    @NotBlank
    String title;
}
