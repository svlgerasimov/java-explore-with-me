package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;
import ru.practicum.ewm.stats.model.StatEntity;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StatServiceImplIntegrationTest {

    private final StatServiceImpl statService;
    private final EntityManager em;

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String app1 = "service1";
    private static final String app2 = "service2";
    private static final String uri1 = "uri1";
    private static final String uri2 = "uri2";
    private static final String uri3 = "uri3";

    @Test
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void saveTest() {
        String app = "ewm-main-service";
        String ip = "192.163.0.1";
        String uri = "/events/1";
        LocalDateTime timestamp = LocalDateTime.parse("2022-09-06 11:00:23", dateTimeFormatter);
        StatDtoIn dto = StatDtoIn.builder()
                .app(app)
                .ip(ip)
                .uri(uri)
                .timestamp(timestamp)
                .build();

        statService.save(dto);

        List<StatEntity> statEntities =
                em.createQuery("select s from StatEntity s", StatEntity.class)
                        .getResultList();
        assertThat(statEntities).hasSize(1);
        assertThat(statEntities.get(0)).hasNoNullFieldsOrProperties()
                .extracting("app", "ip", "uri", "timestamp")
                .containsExactly(app, ip, uri, timestamp);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllNonUniqueHitsWithNoHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2030, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                Collections.emptyList(), false);

        assertThat(statDtos).hasSize(0);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllUniqueHitsWithNoHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2030, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                Collections.emptyList(), true);

        assertThat(statDtos).hasSize(0);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllNonUniqueHitsWithAllHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                Collections.emptyList(), false);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(4L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(4L).build(),
                StatDtoOut.builder().app(app1).uri(uri2).hits(4L).build(),
                StatDtoOut.builder().app(app2).uri(uri2).hits(4L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(4L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(4L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllUniqueHitsWithAllHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                Collections.emptyList(), true);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app1).uri(uri2).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri2).hits(2L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(2L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllNonUniqueHitsWithHalfHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2015, 1, 1, 0, 0),
                Collections.emptyList(), false);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app1).uri(uri2).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri2).hits(2L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(2L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllUniqueHitsWithHalfHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2015, 1, 1, 0, 0),
                Collections.emptyList(), true);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(1L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(1L).build(),
                StatDtoOut.builder().app(app1).uri(uri2).hits(1L).build(),
                StatDtoOut.builder().app(app2).uri(uri2).hits(1L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(1L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(1L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getNonUniqueHitsWithUrisWithNoHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2030, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                List.of(uri1, uri3), false);

        assertThat(statDtos).hasSize(0);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUniqueHitsWithUrisWithNoHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2030, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                List.of(uri1, uri3), true);

        assertThat(statDtos).hasSize(0);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getNonUniqueHitsWithUrisWithAllHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                List.of(uri1, uri3), false);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(4L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(4L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(4L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(4L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUniqueHitsWithUrisWithAllHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2035, 1, 1, 0, 0),
                List.of(uri1, uri3), true);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(2L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getNonUniqueHitsWithUrisWithHalfHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2015, 1, 1, 0, 0),
                List.of(uri1, uri3), false);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(2L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(2L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(2L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }

    @Test
    @Sql(scripts = "/get-stats-prepare.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/clear-stats.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUniqueHitsWithUrisWithHalfHitsInTimeInterval() {
        List<StatDtoOut> statDtos = statService.get(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2015, 1, 1, 0, 0),
                List.of(uri1, uri3), true);

        List<StatDtoOut> expected = List.of(
                StatDtoOut.builder().app(app1).uri(uri1).hits(1L).build(),
                StatDtoOut.builder().app(app2).uri(uri1).hits(1L).build(),
                StatDtoOut.builder().app(app1).uri(uri3).hits(1L).build(),
                StatDtoOut.builder().app(app2).uri(uri3).hits(1L).build());

        assertThat(statDtos).hasSameElementsAs(expected);
    }
}