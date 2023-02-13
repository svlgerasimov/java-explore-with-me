package ru.practicum.ewm.main.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.request.dto.RequestDtoOut;

public interface RequestService {
    @Transactional
    RequestDtoOut add(Long userId, Long eventId);

    @Transactional
    RequestDtoOut cancelRequest(Long userId, Long requestId);
}
