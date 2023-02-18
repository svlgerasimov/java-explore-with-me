package ru.practicum.ewm.main.request.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Value
@Builder
@Jacksonized
public class RequestStatusUpdateDtoIn {

    @NotEmpty
    List<Long> requestIds;

    @NotNull
    RequestStatusAction status;
}
