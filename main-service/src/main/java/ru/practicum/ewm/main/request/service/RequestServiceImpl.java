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
import ru.practicum.ewm.main.exception.NotImplementedException;
import ru.practicum.ewm.main.request.dto.*;
import ru.practicum.ewm.main.request.model.RequestDtoMapper;
import ru.practicum.ewm.main.request.model.RequestEntity;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.user.model.UserEntity;
import ru.practicum.ewm.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        Long eventInitiatorId = eventEntity.getInitiator().getId();
        if (eventInitiatorId.equals(userId)) {
            throw new ConditionsNotMetException("Initiator of event can't request participation in it");
        }

        EventState eventState = eventEntity.getState();
        if (!EventState.PUBLISHED.equals(eventState)) {
            throw new ConditionsNotMetException("Event is not published. Request can't be added.");
        }

        int participantLimit = eventEntity.getParticipantLimit();
        if (participantLimit != 0) {
            int curParticipants = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
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
            requestEntity.setStatus(RequestStatus.PENDING);
        } else {
            requestEntity.setStatus(RequestStatus.CONFIRMED);
        }

        requestEntity = requestRepository.save(requestEntity);

        RequestDtoOut requestDtoOut = requestDtoMapper.toDto(requestEntity);
        log.debug("Add request {}", requestDtoOut);

        return requestDtoOut;
    }

    @Override
    @Transactional
    public RequestDtoOut cancelRequest(Long userId, Long requestId) {
        RequestEntity requestEntity = findRequestEntityByIdAndRequesterId(requestId, userId);
        requestEntity.setStatus(RequestStatus.CANCELED);
        RequestDtoOut requestDtoOut = requestDtoMapper.toDto(requestEntity);
        log.debug("Request canceled by requester: " + requestDtoOut);
        return  requestDtoOut;
    }

    @Override
    public List<RequestDtoOut> findAllByRequesterId(Long requesterId) {
        return requestDtoMapper.toDto(requestRepository.findAllByRequesterId(requesterId));
    }

    @Override
    public List<RequestDtoOut> findAllByEvent(Long userId, Long eventId) {
        findEventEntityByIdAndInitiatorId(eventId, userId);
        return requestDtoMapper.toDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public RequestStatusUpdateDtoOut updateStatusByEventInitiator(Long initiatorId,
                                                                  Long eventId,
                                                                  RequestStatusUpdateDtoIn statusUpdateDto) {
        EventEntity eventEntity = findEventEntityByIdAndInitiatorId(eventId, initiatorId);
        List<Long> requestIds = statusUpdateDto.getRequestIds();
        List<RequestEntity> requestEntities = requestRepository.findAllByIdIn(requestIds);
        List<RequestEntity> confirmedRequests = new ArrayList<>();
        List<RequestEntity> rejectedRequests = new ArrayList<>();
        RequestStatusAction statusAction = statusUpdateDto.getStatus();
        switch (statusAction) {
            case CONFIRMED:
                for (RequestEntity requestEntity : requestEntities) {
                    RequestStatus requestStatus = requestEntity.getStatus();
                    if (RequestStatus.PENDING.equals(requestStatus)) {
                        confirmedRequests.add(requestEntity);
                    } else if (!RequestStatus.CONFIRMED.equals(requestStatus)) {
                        throw new ConditionsNotMetException(
                                "Request with id=" + requestEntity.getId() + ": request must be in pending state.");
                    }
                }
                int curParticipants = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
                int participantsLimit = eventEntity.getParticipantLimit();
                if (participantsLimit != 0 && participantsLimit < curParticipants + confirmedRequests.size()) {
                    throw new ConditionsNotMetException("Confirming requests would exceed participants limit.");
                }
                confirmedRequests.forEach(
                        requestEntity -> requestEntity.setStatus(RequestStatus.CONFIRMED)
                );
                if (curParticipants + confirmedRequests.size() >= participantsLimit) {
                    requestRepository.replaceStatus(eventId, RequestStatus.PENDING, RequestStatus.REJECTED);
                }
                break;
            case REJECTED:
                for (RequestEntity requestEntity : requestEntities) {
                    RequestStatus requestStatus = requestEntity.getStatus();
                    if (RequestStatus.PENDING.equals(requestStatus)) {
                        requestEntity.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(requestEntity);
                    } else if (!RequestStatus.REJECTED.equals(requestStatus)) {
                        throw new ConditionsNotMetException(
                                "Request with id=" + requestEntity.getId() + ": request must be in pending state.");
                    }
                }
                break;
            default:
                throw new NotImplementedException("Set request status to " + statusAction + " is not implemented.");
        }
        return requestDtoMapper.toUpdateDto(confirmedRequests, rejectedRequests);
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

    private EventEntity findEventEntityByIdAndInitiatorId(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Event with id=" + eventId + " and initiator id=" + userId + " was not found."));
    }
}
