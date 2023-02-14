package ru.practicum.ewm.main.request.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class RequestStatusUpdateDto {

    List<Long> requestIds;

    RequestState status;
}
