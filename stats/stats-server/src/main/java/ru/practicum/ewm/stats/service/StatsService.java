package ru.practicum.ewm.stats.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.StatsDto;

public interface StatsService {

    void saveHit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime start,
                            LocalDateTime end,
                            List<String> uris,
                            boolean unique);
}