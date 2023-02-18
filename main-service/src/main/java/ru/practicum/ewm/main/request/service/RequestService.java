package ru.practicum.ewm.main.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;
import ru.practicum.ewm.main.request.dto.RequestStatusUpdateDtoIn;
import ru.practicum.ewm.main.request.dto.RequestStatusUpdateDtoOut;

import java.util.List;

public interface RequestService {
    @Transactional
    RequestDtoOut add(Long userId, Long eventId);

    @Transactional
    RequestDtoOut cancelRequest(Long userId, Long requestId);

    List<RequestDtoOut> findAllByRequesterId(Long requesterId);

    List<RequestDtoOut> findAllByEvent(Long userId, Long eventId);

    RequestStatusUpdateDtoOut updateStatusByEventInitiator(Long initiatorId,
                                                           Long eventId,
                                                           RequestStatusUpdateDtoIn statusUpdateDto);
}
