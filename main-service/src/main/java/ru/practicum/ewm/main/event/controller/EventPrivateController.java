package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoOut post(@PathVariable Long userId,
                            @Valid @RequestBody EventDtoIn eventDtoIn) {
        return eventService.add(userId, eventDtoIn);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDtoOutShort> findAllByInitiatorId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.findAllByInitiatorId(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDtoOut findByEventIdAndInitiatorId(
            @PathVariable Long eventId,
            @PathVariable Long userId) {
        return eventService.findByEventIdAndInitiatorId(eventId, userId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDtoOut patchByInitiator(
            @PathVariable Long eventId,
            @PathVariable Long userId,
            @RequestBody @Valid EventDtoInInitiatorPatch eventDtoInInitiatorPatch) {
        return eventService.patchByInitiator(eventId, userId, eventDtoInInitiatorPatch);
    }
}
