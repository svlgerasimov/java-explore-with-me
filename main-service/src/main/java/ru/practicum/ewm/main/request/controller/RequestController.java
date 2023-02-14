package ru.practicum.ewm.main.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;
import ru.practicum.ewm.main.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RequestController {

    private final RequestService requestService;

    // Private API

    @GetMapping("/users/{userId}/requests")
    public List<RequestDtoOut> findAllByRequester(@PathVariable Long userId) {
        return requestService.findAllByRequesterId(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDtoOut post(@PathVariable Long userId,
                              @RequestParam Long eventId) {
        return requestService.add(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDtoOut cancelRequest(@PathVariable Long userId,
                                @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDtoOut> findAllByEvent(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return requestService.findAllByEvent(userId, eventId);
    }
}
