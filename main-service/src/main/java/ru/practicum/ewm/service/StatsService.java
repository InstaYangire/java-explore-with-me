package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.HitDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsClient statsClient;

    public void hit(HttpServletRequest request) {
        HitDto dto = HitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.saveHit(dto);
    }
}