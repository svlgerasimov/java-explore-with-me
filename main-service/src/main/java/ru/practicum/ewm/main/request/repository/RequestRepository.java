package ru.practicum.ewm.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.request.dto.RequestStatus;
import ru.practicum.ewm.main.request.model.RequestEntity;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity, Long>,
        RequestCustomRepository {

    @Query (value = "SELECT COUNT(rq) FROM RequestEntity as rq " +
            "WHERE rq.event.id=:eventId AND rq.status=:status")
    int countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") RequestStatus status);

    interface CountById {

        Long getId();

        Integer getCount();
    }

    @Query (value = "SELECT rq.event.id as id, COUNT(rq) as count " +
            "FROM RequestEntity as rq " +
            "WHERE rq.event.id in :eventIds AND rq.status=:status " +
            "GROUP BY rq.event.id")
    List<CountById> countByEventIdInAndStatus(@Param("eventIds") List<Long> eventIds,
                                        @Param("status") RequestStatus status);

    Optional<RequestEntity> findByIdAndRequesterId(Long requestId, Long requesterId);

    List<RequestEntity> findAllByRequesterId(Long requesterId);

    List<RequestEntity> findAllByIdIn(List<Long> requestIds);

    List<RequestEntity> findAllByEventId(Long eventId);

    @Modifying
    @Query(value = "UPDATE RequestEntity r SET r.status=:newStatus " +
            "WHERE r.event.id=:eventId and r.status=:oldStatus")
    void replaceStatus(@Param("eventId") Long eventId,
                        @Param("oldStatus") RequestStatus oldStatus,
                        @Param("newStatus") RequestStatus newStatus);
}
