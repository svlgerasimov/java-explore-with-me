package ru.practicum.ewm.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.event.dto.EventState;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.ConditionsNotMetException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;
import ru.practicum.ewm.main.request.dto.RequestState;
import ru.practicum.ewm.main.request.model.RequestDtoMapper;
import ru.practicum.ewm.main.request.model.RequestEntity;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.user.model.UserEntity;
import ru.practicum.ewm.main.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final RequestDtoMapper requestDtoMapper;

    @Override
    @Transactional
    public RequestDtoOut add(Long userId, Long eventId) {
        EventEntity eventEntity = findEventEntity(eventId);

        Long eventInitiatorId = eventEntity.getId();
        if (eventInitiatorId.equals(userId)) {
            throw new ConditionsNotMetException("Initiator of event can't request participation in it");
        }

        EventState eventState = eventEntity.getState();
        if (!EventState.PUBLISHED.equals(eventState)) {
            throw new ConditionsNotMetException("Event is not published. Request can't be added.");
        }

        int participantLimit = eventEntity.getParticipantLimit();
        if (participantLimit != 0) {
            int curParticipants = requestRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED);
            if (curParticipants >= participantLimit) {
                throw new ConditionsNotMetException("Limit of participants already reached.");
            }
        }

        UserEntity userEntity = findUserEntity(userId);

        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setRequester(userEntity);
        requestEntity.setEvent(eventEntity);
        requestEntity.setCreated(LocalDateTime.now());

        if (eventEntity.getRequestModeration()) {
            requestEntity.setStatus(RequestState.PENDING);
        } else {
            requestEntity.setStatus(RequestState.CONFIRMED);
        }

        RequestDtoOut requestDtoOut = requestDtoMapper.toDto(requestEntity);
        log.debug("Add request {}", requestDtoOut);

        return requestDtoOut;
    }

    @Override
    @Transactional
    public RequestDtoOut cancelRequest(Long userId, Long requestId) {
        RequestEntity requestEntity = findRequestEntityByIdAndRequesterId(requestId, userId);
        requestEntity.setStatus(RequestState.CANCELED);
        RequestDtoOut requestDtoOut = requestDtoMapper.toDto(requestEntity);
        log.debug("Request canceled by requester: " + requestDtoOut);
        return  requestDtoOut;
    }

    private RequestEntity findRequestEntity(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request with id=" + id + " was not found."));
    }

    private RequestEntity findRequestEntityByIdAndRequesterId(Long requestId, Long requesterId) {
        return requestRepository.findByIdAndRequesterId(requestId, requesterId)
                .orElseThrow(() -> new NotFoundException(
                        "Request with id=" + requestId + " and requester id=" + requesterId + " was not found."
                ));
    }

    private UserEntity findUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found."));
    }

    private EventEntity findEventEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));
    }

}
