package ru.practicum.ewm.main.event.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.main.event.dto.EventState;
import ru.practicum.ewm.main.event.model.EventEntity;
import ru.practicum.ewm.main.event.model.QEventEntity;
import ru.practicum.ewm.main.request.model.QRequestEntity;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventCustomRepositoryImpl implements EventCustomRepository {

    private final EntityManager em;

    @Override
    public List<EventEntity> findByFiltersAdmin(List<Long> users,
                                                List<EventState> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Integer from,
                                                Integer size) {

        BooleanBuilder filtersBuilder = new BooleanBuilder();

        QEventEntity qEventEntity = QEventEntity.eventEntity;

        if (users != null) {
            filtersBuilder.and(qEventEntity.initiator.id.in(users));
        }
        if (states != null) {
            filtersBuilder.and(qEventEntity.state.in(states));
        }
        if (categories != null) {
            filtersBuilder.and(qEventEntity.category.id.in(categories));
        }
        if (rangeStart != null) {
            filtersBuilder.and(qEventEntity.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            filtersBuilder.and(qEventEntity.eventDate.before(rangeEnd));
        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<EventEntity> query = queryFactory
                .selectFrom(qEventEntity)
                .leftJoin(qEventEntity.category).fetchJoin()
                .leftJoin(qEventEntity.initiator).fetchJoin()
                .where(filtersBuilder)
                .offset(from)
                .limit(size);

        return query.fetch();
    }

    @Override
    public List<EventEntity> findPublishedEventsByFiltersOrderByDate(String text, List<Long> categories, Boolean paid,
                                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                               Boolean onlyAvailable,
                                                               Integer from, Integer size) {

        QEventEntity qEventEntity = QEventEntity.eventEntity;
        BooleanBuilder filtersBuilder = predicateForPublishedEventsWithFilters(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<EventEntity> query = queryFactory
                .selectFrom(qEventEntity)
                .innerJoin(qEventEntity.category).fetchJoin()
                .innerJoin(qEventEntity.initiator).fetchJoin()
                .where(filtersBuilder)
                .orderBy(qEventEntity.eventDate.asc())
                .offset(from)
                .limit(size);

        return query.fetch();
    }

    @Override
    public List<Long> findIdsOfPublishedEventsByFilters(String text, List<Long> categories, Boolean paid,
                                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                          Boolean onlyAvailable) {

        QEventEntity qEventEntity = QEventEntity.eventEntity;
        BooleanBuilder filtersBuilder = predicateForPublishedEventsWithFilters(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<Long> query = queryFactory
                .select(qEventEntity.id)
                .from(qEventEntity)
                .where(filtersBuilder);

        return query.fetch();
    }

    private BooleanBuilder predicateForPublishedEventsWithFilters(String text, List<Long> categories, Boolean paid,
                                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                                  Boolean onlyAvailable) {
        QEventEntity qEventEntity = QEventEntity.eventEntity;
        BooleanBuilder filtersBuilder = new BooleanBuilder(qEventEntity.state.eq(EventState.PUBLISHED));

        if (text != null) {
            filtersBuilder.and(
                    qEventEntity.annotation.containsIgnoreCase(text)
                            .or(qEventEntity.description.containsIgnoreCase(text))
            );
        }
        if (categories != null) {
            filtersBuilder.and(qEventEntity.category.id.in(categories));
        }
        if (paid != null) {
            filtersBuilder.and(qEventEntity.paid.eq(paid));
        }
        if (rangeStart == null && rangeEnd == null) {
            filtersBuilder.and(qEventEntity.eventDate.after(LocalDateTime.now()));
        }
        if (rangeStart != null) {
            filtersBuilder.and(qEventEntity.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            filtersBuilder.and(qEventEntity.eventDate.before(rangeEnd));
        }
        if (onlyAvailable) {
            QRequestEntity qRequestEntity = QRequestEntity.requestEntity;
            JPQLQuery<Long> subQueryIds = JPAExpressions
                    .select(qEventEntity.id)
                    .from(qEventEntity)
                    .leftJoin(qRequestEntity).on(qRequestEntity.event.eq(qEventEntity))
                    .where(filtersBuilder)
                    .groupBy(qEventEntity)
                    .having(
                            qEventEntity.participantLimit.eq(0)
                                    .or(qRequestEntity.count().lt(qEventEntity.participantLimit))

                    );
            filtersBuilder = new BooleanBuilder(qEventEntity.id.in(subQueryIds));
        }
        return filtersBuilder;
    }

}
