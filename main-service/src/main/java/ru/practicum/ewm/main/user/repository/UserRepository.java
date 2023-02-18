package ru.practicum.ewm.main.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.user.model.UserEntity;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAllByIdIn(Collection<Long> id, Pageable pageable);
}
