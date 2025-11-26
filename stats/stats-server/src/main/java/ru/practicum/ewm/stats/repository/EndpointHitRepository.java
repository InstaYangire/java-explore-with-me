package ru.practicum.ewm.stats.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.model.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
            SELECT new ru.practicum.ewm.stats.dto.ViewStatsDto(
                h.app,
                h.uri,
                COUNT(h.ip)
            )
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
              AND (:uris IS NULL OR h.uri IN :uris)
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h.ip) DESC
            """)
    List<ViewStatsDto> getStats(LocalDateTime start,
                                LocalDateTime end,
                                List<String> uris);

    @Query("""
            SELECT new ru.practicum.ewm.stats.dto.ViewStatsDto(
                h.app,
                h.uri,
                COUNT(DISTINCT h.ip)
            )
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
              AND (:uris IS NULL OR h.uri IN :uris)
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStatsDto> getUniqueStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris);
}