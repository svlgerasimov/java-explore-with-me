package ru.practicum.ewm.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.event.service.EventSortType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class EventPublicController {

    private final EventService eventService;

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
            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") EventSortType sortType,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest httpServletRequest) {
        return eventService.findPublishedEventsByFilters(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sortType, from, size, httpServletRequest);
    }

}
