package ru.practicum.ewm.stats.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.model.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<Object[]> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<Object[]> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}