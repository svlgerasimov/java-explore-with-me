package ru.practicum.ewm.main.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.categories.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
