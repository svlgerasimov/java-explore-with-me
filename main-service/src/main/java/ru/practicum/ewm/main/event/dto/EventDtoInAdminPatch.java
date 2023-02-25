package ru.practicum.ewm.main.event.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@EqualsAndHashCode(callSuper = true)
@Value
@SuperBuilder
@Jacksonized
public class EventDtoInAdminPatch extends EventDtoInPatch {

    EventStateAdminAction stateAction;

    String rejectionComment;
}
