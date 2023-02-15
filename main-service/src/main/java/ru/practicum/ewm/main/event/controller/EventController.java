package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.event.service.EventSortType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class EventController {

    private final EventService eventService;

    // Private API

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

    // Admin API

    @PatchMapping("/admin/events/{eventId}")
    public EventDtoOut patchByAdmin(
            @PathVariable Long eventId,
            @RequestBody @Valid EventDtoInAdminPatch eventDtoInAdminPatch) {
        return eventService.patchByAdmin(eventId, eventDtoInAdminPatch);
    }

    @GetMapping("/admin/events")
    public List<EventDtoOut> findByFiltersAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.findByFiltersAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    // Public API

    @GetMapping("/events/{id}")
    public EventDtoOut findPublishedEventById(@PathVariable("id") Long eventId,
                                              HttpServletRequest httpServletRequest) {
        return eventService.findPublishedEventById(eventId, httpServletRequest);
    }

    @GetMapping("/events")
    public List<EventDtoOutShort> findPublishedEventsByFilters(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort") EventSortType sortType,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest httpServletRequest) {
        return eventService.findPublishedEventsByFilters(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sortType, from, size, httpServletRequest);
    }

}
