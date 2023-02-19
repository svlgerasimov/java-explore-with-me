package ru.practicum.ewm.main.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.event.dto.EventState;
import ru.practicum.ewm.main.event.model.EventEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository
        extends JpaRepository<EventEntity, Long>,
        EventCustomRepository {

    @Query(value = "select e from EventEntity e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.state=:state and e.id=:eventId")
    Optional<EventEntity> findByIdAndState(@Param("eventId") Long eventId,
                                           @Param("state") EventState state);

    @Query(value = "select e from EventEntity e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.id = :eventId and e.initiator.id = :initiatorId")
    Optional<EventEntity> findByIdAndInitiatorId(@Param("eventId") Long eventId,
                                                 @Param("initiatorId") Long initiatorId);

    @Query(value = "select e from EventEntity e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.initiator.id=:initiatorId")
    List<EventEntity> findAllByInitiatorId(@Param("initiatorId") Long initiatorId,
                                           Pageable pageable);

    @Query(value = "select e from EventEntity e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.id in :eventIds")
    List<EventEntity> findAllByIdIn(Collection<Long> eventIds);

    List<EventEntity> findAllByState(EventState eventState, Pageable pageable);
}
