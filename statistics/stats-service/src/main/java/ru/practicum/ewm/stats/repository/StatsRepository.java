package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.model.StatEntity;

public interface StatsRepository extends JpaRepository<StatEntity, Long> {

//    @Query("SELECT new ru.practicum.ewm.stats.dto.StatDtoOut(s.app, s.uri, COUNT(s.ip)) " +
//            "FROM StatEntity as s " +
//            "WHERE s.timestamp BETWEEN :start AND :end " +
//            "GROUP BY s.app, s.uri")
//    List<StatDtoOut> getAllStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
//
//    @Query("SELECT new ru.practicum.ewm.stats.dto.StatDtoOut(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
//            "FROM StatEntity as s " +
//            "WHERE s.timestamp BETWEEN :start AND :end " +
//            "GROUP BY s.app, s.uri")
//    List<StatDtoOut> getAllStatisticsUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
//
//    @Query("SELECT new ru.practicum.ewm.stats.dto.StatDtoOut(s.app, s.uri, COUNT(s.ip)) " +
//            "FROM StatEntity as s " +
//            "WHERE s.timestamp BETWEEN :start AND :end " +
//            "AND s.uri IN :uris " +
//            "GROUP BY s.app, s.uri")
//    List<StatDtoOut> getUrisStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
//                                       @Param("uris") List<String> uris);
//
//    @Query("SELECT new ru.practicum.ewm.stats.dto.StatDtoOut(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
//            "FROM StatEntity as s " +
//            "WHERE s.timestamp BETWEEN :start AND :end " +
//            "AND s.uri IN :uris " +
//            "GROUP BY s.app, s.uri")
//    List<StatDtoOut> getUrisStatisticsUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
//                                             @Param("uris") List<String> uris);

}
