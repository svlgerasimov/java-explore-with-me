package ru.practicum.ewm.main.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;
import ru.practicum.ewm.main.request.dto.RequestStatusUpdateDtoIn;
import ru.practicum.ewm.main.request.dto.RequestStatusUpdateDtoOut;
import ru.practicum.ewm.main.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RequestPrivateController {

    private final RequestService requestService;

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

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public RequestStatusUpdateDtoOut updateStatusByEventInitiator(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid RequestStatusUpdateDtoIn requestStatusUpdateDtoIn) {
        return requestService.updateStatusByEventInitiator(userId, eventId, requestStatusUpdateDtoIn);
    }
}
