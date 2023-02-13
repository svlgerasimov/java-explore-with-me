package ru.practicum.ewm.main.event.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
@Jacksonized
public class EventDtoInAdminPatch extends EventDtoInPatch {

    @NotNull
    EventStateAdminAction stateAction;
}
