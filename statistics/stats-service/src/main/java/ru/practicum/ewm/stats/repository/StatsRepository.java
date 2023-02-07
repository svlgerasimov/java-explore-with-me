package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.model.StatEntity;

public interface StatsRepository extends JpaRepository<StatEntity, Long> {

}
