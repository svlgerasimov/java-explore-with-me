package ru.practicum.ewm.main.user.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserDtoOutShort {
    Long id;
    String name;
}
