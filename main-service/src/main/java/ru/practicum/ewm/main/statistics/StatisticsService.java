package ru.practicum.ewm.main.statistics;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

public interface StatisticsService {
    void hitToStatistics(HttpServletRequest httpServletRequest);

    Long getViewsCountFromStatistics(Long eventId);

    Map<Long, Long> getViewsCountByEventIdFromStatistics(Collection<Long> eventIds);
}
