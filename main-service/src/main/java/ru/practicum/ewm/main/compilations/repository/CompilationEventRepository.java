package ru.practicum.ewm.main.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.compilations.model.CompilationEntity;
import ru.practicum.ewm.main.compilations.model.CompilationEventEntity;
import ru.practicum.ewm.main.event.model.EventEntity;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEventEntity, Long> {

    void deleteAllByCompilation(CompilationEntity compilation);

    @Query(value = "select e from CompilationEventEntity ce " +
            "join ce.event e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where ce.compilation=:compilation")
    List<EventEntity> findEventsByCompilation(@Param("compilation") CompilationEntity compilation);

    interface EventsIdsByCompilationId {

        Long getCompId();

        Long getEventId();
    }

    @Query(value = "select ce.compilation.id as compId, ce.event.id as eventId " +
            "from CompilationEventEntity ce " +
            "where ce.compilation.id in :compIds")
    List<EventsIdsByCompilationId> findEventsByCompilationIdsIn(@Param("compIds") List<Long> compIds);
}
