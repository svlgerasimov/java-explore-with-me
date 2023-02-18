package ru.practicum.ewm.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.stats.dto.StatDtoOut;
import ru.practicum.ewm.stats.model.StatEntity;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StatsCustomRepositoryImpl implements StatsCustomRepository {

    private final EntityManager entityManager;

    @Override
    public List<StatDtoOut> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StatDtoOut> query = cb.createQuery(StatDtoOut.class);
        Root<StatEntity> root = query.from(StatEntity.class);
        Path<Object> ip = root.get("ip");
        Path<Object> app = root.get("app").get("name");
        Path<Object> uri = root.get("uri");
        Path<LocalDateTime> timestamp = root.get("timestamp");
        Expression<Long> count =
                unique ? cb.countDistinct(ip) : cb.count(ip);
        query.multiselect(app, uri, count);
        Predicate where = cb.between(timestamp, start, end);
        if (!uris.isEmpty()) {
            where = cb.and(where, uri.in(uris));
        }
        query.where(where);
        query.groupBy(app, uri);
        query.orderBy(cb.desc(count));

        return entityManager.createQuery(query).getResultList();
    }
}
