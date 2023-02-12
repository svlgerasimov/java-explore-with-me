package ru.practicum.ewm.main.event.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class LocationDto {

    @NotNull
    Double lat; // latitude

    @NotNull
    Double lon; // longitude
}
