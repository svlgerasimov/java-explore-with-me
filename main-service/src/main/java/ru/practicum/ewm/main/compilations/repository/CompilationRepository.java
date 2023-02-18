package ru.practicum.ewm.main.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.main.compilations.model.CompilationEntity;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Long>,
        QuerydslPredicateExecutor<CompilationEntity> {

}
