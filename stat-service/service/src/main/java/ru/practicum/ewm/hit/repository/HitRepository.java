package ru.practicum.ewm.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.hit.model.Hit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(h.id)) " +
            "FROM Hit h " +
            "WHERE h.uri IN :uris " +
            "AND h.timestamp BETWEEN :start and :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(h.id) DESC ")
    List<ViewStats> getStatsWithUrisNotUnique(@Param("uris") List<String> uris,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(h.id)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start and :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(h.id) DESC ")
    List<ViewStats> getStatsWithoutUrisNotUnique(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start and :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(h.id) DESC ")
    List<ViewStats> getStatsWithoutUrisUnique(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM Hit h " +
            "WHERE h.uri IN :uris " +
            "AND h.timestamp BETWEEN :start and :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY count(h.id) DESC ")
    List<ViewStats> getStatsWithUrisUnique(@Param("uris") List<String> uris,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
