package ru.practicum.ewm.stats.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.StatsDto;
import ru.practicum.ewm.stats.mapper.EndpointHitMapper;
import ru.practicum.ewm.stats.model.EndpointHit;
import ru.practicum.ewm.stats.repository.EndpointHitRepository;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository repository;
    private final EndpointHitMapper mapper;

    @Override
    public void saveHit(HitDto hitDto) {
        EndpointHit hit = mapper.toEntity(hitDto);
        repository.save(hit);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start,
                                   LocalDateTime end,
                                   List<String> uris,
                                   boolean unique) {

        List<Object[]> result;

        if (unique) {
            result = repository.getUniqueStats(start, end, uris);
        } else {
            result = repository.getStats(start, end, uris);
        }

        return result.stream()
                .map(r -> new StatsDto(
                        (String) r[0],
                        (String) r[1],
                        ((Number) r[2]).longValue()
                ))
                .toList();
    }
}