package ru.practicum.ewm.main.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.dto.StatDtoIn;
import ru.practicum.ewm.stats.dto.StatDtoOut;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final StatClient statClient;

    private static final LocalDateTime MIN_DATE =
            LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime MAX_DATE =
            LocalDateTime.of(9999, 12, 31, 23, 59);

    private static final boolean UNIQUE_VIEWS = false;
    @Value("${statistics.app_name}")
    private String appName;

    @Override
    public void hitToStatistics(HttpServletRequest httpServletRequest) {
        try {
            statClient.saveHit(
                    StatDtoIn.builder()
                            .app(appName)
                            .uri(httpServletRequest.getRequestURI())
                            .ip(httpServletRequest.getRemoteAddr())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        } catch (WebClientException e) {
            log.warn("Save endpoint hit by statistics client was not successful.", e);
        } catch (Throwable e) {
            log.warn("Unexpected error while saving endpoint by statistics client.", e);
        }
    }

    @Override
    public Long getViewsCountFromStatistics(Long eventId) {
        String uri = "/events/" + eventId;
        List<StatDtoOut> stats;
        try {
            stats = statClient.getStats(
                    MIN_DATE,
                    MAX_DATE,
                    List.of(uri),
                    UNIQUE_VIEWS);
        } catch (WebClientException e) {
            log.warn("Get views count of uri " + uri + "by statistics client was not successful.", e);
            return 0L;
        } catch (Throwable e) {
            log.warn("Unexpected error while getting views count of uri " + uri + " by statistics client.", e);
            return 0L;
        }
        if (stats.isEmpty()) {
            return 0L;
        }
        if (stats.size() > 1 || !uri.equals(stats.get(0).getUri())) {
            log.warn("Strange response from statistics server. Requested uri=" + uri + ". Response: " + stats);
            return 0L;
        }
        return stats.get(0).getHits();
    }

    @Override
    public Map<Long, Long> getViewsCountByEventIdFromStatistics(Collection<Long> eventIds) {

        List<String> uris = eventIds.stream()
                .map(eventId -> "/events/" + eventId)
                .collect(Collectors.toList());

        List<StatDtoOut> stats;
        try {
            stats = statClient.getStats(
                    MIN_DATE,
                    MAX_DATE,
                    uris,
                    UNIQUE_VIEWS);
        } catch (WebClientException e) {
            log.warn("Get views count of uris by statistics client was not successful.", e);
            return Map.of();
        } catch (Throwable e) {
            log.warn("Unexpected error while getting views count of uris by statistics client.", e);
            return Map.of();
        }

        try {
            return stats.stream()
                    .collect(Collectors.toMap(
                            statDtoOut -> Long.parseLong(
                                    statDtoOut.getUri().replaceFirst("/events/", "")),
                            StatDtoOut::getHits,
                            (hits1, hits2) -> hits1)
                    );
        } catch (Throwable e) {
            log.warn("Strange response from statistics server. Response: " + stats);
            return Map.of();
        }
    }


}
