package ru.practicum.ewm.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.model.ReviewEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    Optional<ReviewEntity> findFirstByEventOrderByCreatedOnDesc(EventEntity event);

    List<ReviewEntity> findAllByEventIdIn(Collection<Long> eventIds);
}
