package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.model.AppEntity;

public interface AppsRepository extends JpaRepository<AppEntity, String> {
}
