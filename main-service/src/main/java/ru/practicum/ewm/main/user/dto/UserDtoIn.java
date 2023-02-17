package ru.practicum.ewm.main.user.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Value
@Builder
@Jacksonized
public class UserDtoIn {
    @Email
    @NotEmpty
    String email;
    @NotBlank
    String name;
}
