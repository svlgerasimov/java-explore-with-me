package ru.practicum.ewm.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.request.dto.RequestState;
import ru.practicum.ewm.main.request.model.RequestEntity;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {

    int countByEventIdAndStatus(Long eventId, RequestState status);

    Optional<RequestEntity> findByIdAndRequesterId(Long requestId, Long requesterId);

    List<RequestEntity> findAllByRequesterId(Long requesterId);

    List<RequestEntity> findAllByEventId(Long eventId);
}
