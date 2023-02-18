package ru.practicum.ewm.main.event.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class LocationModel {
    private Double latitude;
    private Double longitude;
}
