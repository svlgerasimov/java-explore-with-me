package ru.practicum.ewm.main.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.compilations.model.CompilationEventEntity;

public interface CompilationEventRepository extends JpaRepository<CompilationEventEntity, Long> {
}
