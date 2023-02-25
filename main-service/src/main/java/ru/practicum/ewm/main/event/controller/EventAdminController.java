package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class EventAdminController {

    private final EventService eventService;

    @PatchMapping("/admin/events/{eventId}")
    public EventDtoOutPrivate patchByAdmin(
            @PathVariable Long eventId,
            @RequestBody @Valid EventDtoInAdminPatch eventDtoInAdminPatch) {
        return eventService.patchByAdmin(eventId, eventDtoInAdminPatch);
    }

    @GetMapping("/admin/events")
    public List<EventDtoOutPrivate> findByFiltersAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.findByFiltersAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/admin/events/pending")
    public List<EventDtoOutPrivate> findAllPending(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.findAllPending(from, size);
    }
}
