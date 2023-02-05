package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;
import ru.practicum.ewm.stats.model.StatDtoInMapper;
import ru.practicum.ewm.stats.model.StatEntity;
import ru.practicum.ewm.stats.repository.StatsRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {

    private final EntityManager entityManager;
    private final StatsRepository statsRepository;
    private final StatDtoInMapper statDtoInMapper;

    @Override
    @Transactional
    public void save(StatDtoIn statDtoIn) {
        StatEntity statEntity = statDtoInMapper.fromDto(statDtoIn);
        statsRepository.save(statEntity);
        log.debug("Save statistics hit: " + statEntity);
    }

    @Override
    public List<StatDtoOut> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StatDtoOut> query = cb.createQuery(StatDtoOut.class);
        Root<StatEntity> root = query.from(StatEntity.class);
        Path<Object> ip = root.get("ip");
        Path<Object> app = root.get("app");
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

//        return uris.isEmpty() ?
//                (unique ? statsRepository.getAllStatisticsUnique(start, end)
//                        : statsRepository.getAllStatistics(start, end))
//                : (unique ? statsRepository.getUrisStatisticsUnique(start, end, uris)
//                        : statsRepository.getUrisStatistics(start, end, uris));
    }
}
